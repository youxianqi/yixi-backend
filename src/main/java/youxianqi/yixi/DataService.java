package youxianqi.yixi;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import youxianqi.yixi.consts.ActionType;
import youxianqi.yixi.consts.BoolType;
import youxianqi.yixi.consts.MediaType;
import youxianqi.yixi.model.*;
import youxianqi.yixi.oss.OssConfig;
import youxianqi.yixi.reqres.RequestAddResource;
import youxianqi.yixi.reqres.RequestAddTag;
import youxianqi.yixi.reqres.RequestResourceList;
import youxianqi.yixi.reqres.RequestUserAction;
import youxianqi.yixi.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResourceRepo resourceRepo;

    @Autowired
    ResourceContentRepo resourceContentRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    TagDictRepo tagDictRepo;

    @Autowired
    ResourceUserRepo resourceUserRepo;

    @Autowired
    ResourceUserTagRepo resourceUserTagRepo;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    SQL sqlQuery;

    @Autowired
    AliSmsClient2 smsClient;

    @Autowired
    OssConfig ossConfig;

    public void init() {
        smsClient.init(ossConfig.getOssAccessId(),
                ossConfig.getOssAccessKey(),
                "SMS_183247436",
                "SMS_183242435",
                "cn-shanghai",
                "学习小站");
    }

    public void start() {
    }

    @Scheduled(cron = "0 10 0 * * ?")
    public void dailyReset() {
    }

    public int verifyUser(String username, String password) {
        List<MainUser> users = userRepo.findAll();
        for (MainUser user : users) {
            if (username.equals(user.getUserName())
                    && password.equals(user.getPassword())) {
                return user.getUserId();
            }
        }
        return 0;
    }

    public void changePwd(String username, String password) {
        MainUser user = userRepo.findByUserName(username);
        user.setPassword(password);
        userRepo.save(user);
    }

    @Cacheable(cacheNames = "tag_cache", key = "")
    public List<Map<String, Object>> getTags() {
        logger.info("cache not hit...getTags");
        List<MainTagDict> allTags = tagDictRepo.findAll();
        Map<Byte, List<MainTagDict>> tagsPerResType =
                allTags.stream().collect(Collectors.groupingBy(MainTagDict::getTagResourceType));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Byte resType : tagsPerResType.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("resourceType", resType);
            map.put("tags", tagsPerResType.get(resType));
            result.add(map);
        }
        return result;
    }

    public List<CustomResource> getResourceList(RequestResourceList params) {
        return sqlQuery.queryResourceList(params);
    }

    @Cacheable(cacheNames = "resource_cache", key = "#resourceId")
    public List<CustomResource> queryOneResource(int userId, int resourceId) {
        return sqlQuery.queryResourceListByIds(userId, String.valueOf(resourceId));
    }

    private static void increaseTagCount(MainResource resource, int tagId) {
        Map<String, Integer> tags = null;
        int tagCount = 0;
        if (StringUtils.isEmpty(resource.getTagsJsonD())) {
            tags = new HashMap<>();
        } else {
            tags = JsonUtil.stringToObject(resource.getTagsJsonD(), HashMap.class);
            if (tags.containsKey(String.valueOf(tagId))) {
                tagCount = tags.get(String.valueOf(tagId));
            }
        }
        tags.put(String.valueOf(tagId), tagCount + 1);
        resource.setTagsJsonD(JsonUtil.objectToString(tags, ""));
    }

    private static void decreaseTagCount(MainResource resource, int tagId) {
        Map<String, Integer> tags = null;
        if (StringUtils.isEmpty(resource.getTagsJsonD())) {
            return;
        }
        tags = JsonUtil.stringToObject(resource.getTagsJsonD(), HashMap.class);
        if (!tags.containsKey(String.valueOf(tagId))) {
            return;
        }
        int tagCount = tags.get(String.valueOf(tagId));
        if (tagCount > 0) {
            if (tagCount == 1) {
                tags.remove(String.valueOf(tagId));
            }
            else {
                tags.put(String.valueOf(tagId), tagCount - 1);
            }
        }
        resource.setTagsJsonD(JsonUtil.objectToString(tags, ""));
    }

    @CacheEvict(cacheNames = "resource_user_cache", key="#params.resourceId+','+#params.userId")
    @Transactional
    public boolean doUserAction(RequestUserAction params) {
        Cache cache = cacheManager.getCache("resource_cache");
        cache.evict(params.getResourceId());

        MainResource resource = resourceRepo.findOne(params.getResourceId());
        if (params.isAddNotDelete()) {
            if (params.getActionType() == ActionType.VIEW.getValue()
                    || params.getActionType() == ActionType.LIKE.getValue()
                    || params.getActionType() == ActionType.FAV.getValue()
                    || params.getActionType() == ActionType.COMMENT.getValue()) {
                MainResourceUserR existed = resourceUserRepo.findOneByResourceIdAndUserId(params.getResourceId(), params.getUserId());
                if (existed == null) {
                    existed = new MainResourceUserR();
                    existed.setResourceId(params.getResourceId());
                    existed.setUserId(params.getUserId());
                    existed.setComment("");
                }
                if (params.getActionType() == ActionType.VIEW.getValue()) {
                    existed.setViews(existed.getViews() + 1);
                    resource.setViewsD(resource.getViewsD() + 1);
                }
                if (params.getActionType() == ActionType.LIKE.getValue()) {
                    if (existed.getHasLiked() == BoolType.TRUE.getValue()) {
                        return false;
                    }
                    resource.setLikesD(resource.getLikesD() + 1);
                    existed.setHasLiked(BoolType.TRUE.getValue());
                }
                if (params.getActionType() == ActionType.FAV.getValue()) {
                    if (existed.getHasFaved() == BoolType.TRUE.getValue()) {
                        return false;
                    }
                    resource.setFavsD(resource.getFavsD() + 1);
                    existed.setHasFaved(BoolType.TRUE.getValue());
                }
                if (params.getActionType() == ActionType.COMMENT.getValue()) {
                    existed.setComment(params.getComment());
                    existed.setCommentToUserid(params.getCommentToUserId());
                }
                resourceUserRepo.save(existed);
                resourceRepo.save(resource);
                return true;
            }
            if (params.getActionType() == ActionType.TAG.getValue()) {
                String[] tagIds = params.getTagIds().split(",");

                // first decrease resource's tag count
                List<MainResourceUserTagR> existed = resourceUserTagRepo.findByResourceIdAndUserId(
                        params.getResourceId(), params.getUserId());
                for(MainResourceUserTagR t : existed) {
                    decreaseTagCount(resource, t.getTagId());
                }
                // then delete from table resource-user-tag
                resourceUserTagRepo.deleteByResourceIdAndUserId(params.getResourceId(), params.getUserId());

                // then add back
                for (String strTagId : tagIds) {
                    try {
                        int tagId = Integer.valueOf(strTagId.trim());
                        MainResourceUserTagR v = new MainResourceUserTagR();
                        v.setResourceId(params.getResourceId());
                        v.setUserId(params.getUserId());
                        v.setTagId(tagId);
                        resourceUserTagRepo.save(v);
                        increaseTagCount(resource, tagId);
                    } catch (Exception e) {
                        logger.info(ExceptionUtil.getExceptionStack(e));
                    }
                }
                return true;
            }
        } else {
            if (params.getActionType() == ActionType.VIEW.getValue()
                    || params.getActionType() == ActionType.LIKE.getValue()
                    || params.getActionType() == ActionType.FAV.getValue()
                    || params.getActionType() == ActionType.COMMENT.getValue()) {
                MainResourceUserR existed = resourceUserRepo.findOneByResourceIdAndUserId(params.getResourceId(), params.getUserId());
                if (existed == null) {
                    existed = new MainResourceUserR();
                    existed.setResourceId(params.getResourceId());
                    existed.setUserId(params.getUserId());
                }
                if (params.getActionType() == ActionType.VIEW.getValue()) {
                    if (existed.getViews() <= 0) {
                        return false;
                    }
                    existed.setViews(existed.getViews() - 1);
                    if (resource.getViewsD() > 0) {
                        resource.setViewsD(resource.getViewsD() - 1);
                    }
                }
                if (params.getActionType() == ActionType.LIKE.getValue()) {
                    if (existed.getHasLiked() == BoolType.FALSE.getValue()) {
                        return false;
                    }
                    existed.setHasLiked(BoolType.FALSE.getValue());
                    if (resource.getLikesD() > 0) {
                        resource.setLikesD(resource.getLikesD() - 1);
                    }
                }
                if (params.getActionType() == ActionType.FAV.getValue()) {
                    if (existed.getHasFaved() == BoolType.FALSE.getValue()) {
                        return false;
                    }
                    existed.setHasFaved(BoolType.FALSE.getValue());
                    if (resource.getFavsD() > 0) resource.setFavsD(resource.getFavsD() - 1);
                }
                if (params.getActionType() == ActionType.COMMENT.getValue()) {
                    existed.setComment("");
                    existed.setCommentToUserid(0);
                }
                resourceUserRepo.save(existed);
                resourceRepo.save(resource);
                return true;
            }
            if (params.getActionType() == ActionType.TAG.getValue()) {
                String[] tagIds = params.getTagIds().split(",");
                boolean changed = false;
                for (String strTagId : tagIds) {
                    try {
                        int tagId = Integer.valueOf(strTagId.trim());
                        MainResourceUserTagR existed = resourceUserTagRepo.findOneByResourceIdAndUserIdAndTagId(
                                params.getResourceId(), params.getUserId(), tagId);
                        if (existed != null) {
                            resourceUserTagRepo.delete(existed);
                            decreaseTagCount(resource, tagId);
                            resourceRepo.save(resource);
                            changed = true;
                        }
                    } catch (Exception e) {
                        logger.info(ExceptionUtil.getExceptionStack(e));
                    }
                }
                return changed;
            }
        }
        return false;
    }

    @CacheEvict(cacheNames = "tag_cache", key = "")
    public int doAddTag(RequestAddTag params) {
        MainTagDict existed = tagDictRepo.findByTagNameAndTagResourceType(params.getTagName(), (byte) params.getTagResourceType());
        if (existed != null) {
            return existed.getTagId();
        }
        MainTagDict tag = new MainTagDict();
        tag.setTagName(params.getTagName());
        tag.setTagResourceType((byte) params.getTagResourceType());
        tagDictRepo.save(tag);
        return 0;
    }

    @CacheEvict(cacheNames = "tag_cache", key = "")
    public void doDeleteTag(int tagId) {
        tagDictRepo.delete((long) tagId);
    }

    @CacheEvict(cacheNames = "resource_content_cache", key="#payload.resourceId")
    @Transactional
    public int addResource(RequestAddResource payload) {
        MainResource resource = null;
        if (payload.getResourceId() != null) {
            resource = resourceRepo.getOne(payload.getResourceId());
        }
        else {
            resource = new MainResource();
        }
        resource.setKtreeId(payload.getKtreeId());
        resource.setResourceType((byte) payload.getResourceType());
        resource.setResourceStatus((byte) 2);
        resource.setResourceAccessType((byte) 1);
        resource.setContentTitle(payload.getContentTitle());
        resource.setContentDesc(payload.getContentDesc());

        String pattern = "yyyy-MM-dd";
        Date time = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            time = simpleDateFormat.parse(payload.getContentTime());
        } catch (ParseException e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
        }
        resource.setContentTime(new Timestamp(time.getTime()));

        resource.setContentCount((byte) payload.getContentCount());
        resource.setContentMediaType((byte) payload.getContentMediaType());
        resource.setOwnerUserId(payload.getUserid());

        List<String> urls = new ArrayList<>();
        if (payload.getContentMediaType() == MediaType.VIDEO.getValue()) {
            urls = JsonUtil.stringToObject(payload.getMediaUrlListJson(), List.class);
            if (urls.size() > 0) {
                resource.setContentThumbnail(urls.get(0) + "?x-oss-process=video/snapshot,t_10000,m_fast,w_800");
            }
        }
        else if (payload.getContentMediaType() == MediaType.IMAGE.getValue()) {
            urls = JsonUtil.stringToObject(payload.getMediaUrlListJson(), List.class);
            if (urls.size() > 0) {
                resource.setContentThumbnail(urls.get(0));
            }
        }
        List<String> texts = new ArrayList<>();
        if (payload.getContentMediaType() == MediaType.TEXT.getValue()) {
            texts = JsonUtil.stringToObject(payload.getTextListJson(), List.class);
        }
        List<Map<String, Object>> eiList = new ArrayList<>();
        if (payload.getContentMediaType() == MediaType.EXERCISE_ITEM.getValue()) {
            eiList = JsonUtil.stringToObject(payload.getEiListJson(), List.class);
        }

        if (StringUtils.isNotEmpty(payload.getContentThumbnail())) {
            resource.setContentThumbnail(payload.getContentThumbnail());
        }
        resource = resourceRepo.save(resource);
        logger.info("saved successfully...resourceId={}", resource.getResourceId());

        if (payload.getResourceId() != null) {
            resourceContentRepo.deleteByResourceId(payload.getResourceId());
        }
        for (int i = 0; i < payload.getContentCount(); i++) {
            MainResourceContent content = new MainResourceContent();
            content.setContentSeq((byte) (i + 1));
            content.setResourceId(resource.getResourceId());
            content.setContentMediaType((byte) payload.getContentMediaType());
            if (payload.getContentMediaType() == MediaType.VIDEO.getValue()
                    || payload.getContentMediaType() == MediaType.IMAGE.getValue()) {
                content.setMediaUrl(urls.get(i));
            } else if (payload.getContentMediaType() == MediaType.TEXT.getValue()) {
                content.setTextContent(texts.get(i));
            } else if (payload.getContentMediaType() == MediaType.EXERCISE_ITEM.getValue()) {
                Map<String, Object> eiMap = eiList.get(i);
                if (eiMap.containsKey("question")) {
                    content.setEiQuestion(eiMap.get("question").toString());
                }
                if (eiMap.containsKey("explain")) {
                    content.setEiExplain(eiMap.get("explain").toString());
                }
                if (eiMap.containsKey("correctAnsCnt")) {
                    content.setEiCorrectAnsCnt((byte) Integer.parseInt(eiMap.get("correctAnsCnt").toString()));
                }
                if (eiMap.containsKey("correctAnsListJson")) {
                    content.setEiCorrectAnsListJson(eiMap.get("correctAnsListJson").toString());
                }
            }
            resourceContentRepo.save(content);
        }
        return resource.getResourceId();
    }

    @Cacheable(cacheNames = "resource_content_cache", key="#resourceId")
    public List<MainResourceContent> getResourceContent(int resourceId) {
        logger.info("cache not hit...getResourceContent");
        return resourceContentRepo.findByResourceId(resourceId);
    }

    @CacheEvict(cacheNames = "resource_content_cache", key="#resourceId")
    @Transactional
    public void deleteResource(int resourceId) {
        Cache cache = cacheManager.getCache("resource_cache");
        cache.evict(resourceId);

        resourceRepo.delete(resourceId);
        resourceContentRepo.deleteByResourceId(resourceId);
        resourceUserRepo.deleteByResourceId(resourceId);
        resourceUserTagRepo.deleteByResourceId(resourceId);
    }

    @Cacheable(cacheNames = "resource_user_cache", key="#resourceId+','+#userId")
    public List<Integer> getResourceUserTags(int resourceId, int userId){
        logger.info("cache not hit...getResourceUserTags");
        List<MainResourceUserTagR> r = resourceUserTagRepo.findByResourceIdAndUserId(resourceId, userId);
        List<Integer> ids = new ArrayList<>();
        for(MainResourceUserTagR tag: r){
            ids.add(tag.getTagId());
        }
        return ids;
    }

    @Cacheable(cacheNames = "user_cache", key="#username")
    public boolean existUser(String username) {
        MainUser user = userRepo.findByUserName(username);
        return (user != null);
    }

    @Cacheable(cacheNames = "user_cache", key="#mobile")
    public boolean existMobile(String mobile) {
        MainUser user = userRepo.findByMobile(mobile);
        return (user != null);
    }

    public byte[] newCaptcha(Integer width, Integer height,
                             Integer verifySize, String hash) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String code = CaptchaUtil.outputVerifyImage(width, height, os, verifySize);
            Cache cache = cacheManager.getCache("captcha_cache");
            cache.put(hash, code);
            return os.toByteArray();
        } catch (IOException ioe) {
            logger.info("Failed to generate captcha", ioe);
            return new byte[] {};
        }
    }

    public boolean verifyCaptcha(String hash, String captcha) {
        Cache cache = cacheManager.getCache("captcha_cache");
        String code = cache.get(hash, String.class);
        if (code == null) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(code, captcha);
    }

    public boolean verifyMobileCode(String hash, String mobileCode) {
        Cache cache = cacheManager.getCache("mobileCode_cache");
        String code = cache.get(hash, String.class);
        if (code == null) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(code, mobileCode);
    }

    public Pair<Boolean, String> sendMobileVerifycode(String hash, String captcha, String mobile) {
        if (!verifyCaptcha(hash, captcha))
            return Pair.of(false, "识别码不正确");
        if (StringUtils.isEmpty(mobile)) {
            return Pair.of(false, "手机号为空");
        }
        String mobileCode = RandomUtil.randomInteger(6);
        Pair<Boolean, String> result = smsClient.sendVerify(mobile, mobileCode);
        if (result.getLeft().booleanValue()) {
            Cache cache = cacheManager.getCache("mobileCode_cache");
            cache.put(hash, mobileCode);
        }
        return result;
    }

    public Pair<Boolean,String> registerUser(String userName, String hash, String mobile, String mobileCode, String password) {
        if (userName.trim().length() < 2)
            return Pair.of(false, "用户名长度不符要求");
        if (existUser(userName))
            return Pair.of(false, "用户名已注册");
        if (mobile.trim().length() < 11){
            return Pair.of(false, "手机号长度不符要求");
        }
        if (existMobile(mobile.trim()))
            return Pair.of(false, "手机号已注册");
        if (!verifyMobileCode(hash, mobileCode.trim()))
            return Pair.of(false, "手机验证码不正确");

        MainUser u = new MainUser();
        u.setUserName(userName.trim());
        u.setMobile(mobile.trim());
        u.setPassword(password.trim());
        u.setSexType((byte)0);
        u.setUserStatus((byte)2);
        userRepo.save(u);
        return Pair.of(true,"ok");
    }

    public Pair<Boolean, String> resetPassword(String mobile) {
        MainUser u = userRepo.findByMobile(mobile);
        if (u == null){
            return Pair.of(false, "该手机号未注册");
        }
        String newPwd = CaptchaUtil.generateVerifyCode(8);
        u.setPassword(DigestUtils.md5Hex(newPwd));
        Pair<Boolean, String> r = smsClient.sendResetPassword(mobile, newPwd);
        if (!r.getLeft().booleanValue()){
            return Pair.of(false, "发送重置密码短信失败：" + r.getLeft());
        }
        userRepo.save(u);
        return r;
    }
}
