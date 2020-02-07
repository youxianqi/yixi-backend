package youxianqi.yixi;

import net.sf.json.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import youxianqi.yixi.consts.ActionType;
import youxianqi.yixi.consts.BoolType;
import youxianqi.yixi.consts.MediaType;
import youxianqi.yixi.model.*;
import youxianqi.yixi.reqres.RequestAddResource;
import youxianqi.yixi.reqres.RequestAddTag;
import youxianqi.yixi.reqres.RequestResourceList;
import youxianqi.yixi.reqres.RequestUserAction;
import youxianqi.yixi.utils.ExceptionUtil;
import youxianqi.yixi.utils.JsonUtil;

import javax.persistence.Column;
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
    SQL sqlQuery;

    public void init() {
    }

    public void start() {
//        MainResourceUserTagR r = resourceUserTagRepo.findOneByResourceIdAndUserIdAndTagId(5,1, 403);
//        logger.info(r.toString());
//        RequestResourceList req = new RequestResourceList();
//        req.setKtreeIds("47");
//        req.setResourceType(4);
//        req.setResourceStatus(2);
//        req.setResourceAccessType(1);
//        List<CustomResource> r2 = sqlQuery.queryResourceList(req);
//        logger.info(r2.toString());
    }

    @Scheduled(cron = "0 10 0 * * ?")
    public void dailyReset() {
    }

    public List<Map<String, String>> serverList() {

        List<Map<String, String>> serverList = new ArrayList<>();
        Map<String, String> server = new HashMap<>();
        server.put("IP", "172.16.3.113:8000");
        server.put("appId", "cdh.quickfix_m");
        serverList.add(server);

        Map<String, String> server2 = new HashMap<>();
        server2.put("IP", "172.16.3.112:8000");
        server2.put("appId", "cdh.quickfix_S");
        serverList.add(server2);

        return serverList;
    }

    public boolean verifyUser_demo(String username, String password) {
        List<Pair<String, String>> users = new ArrayList<>();
        users.add(Pair.of("小鱼说", "12345678"));
        users.add(Pair.of("小县城", "12345678")); //25d55ad283aa400af464c76d713c07ad

        for (Pair<String, String> pair : users) {
            if (username.equals(pair.getLeft())
                    && password.equals(DigestUtils.md5Hex(pair.getRight()))) {
                return true;
            }
        }
        return false;
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

    public List<Map<String, Object>> getTags() {
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

    @Transactional
    public boolean doUserAction(RequestUserAction params) {
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

    public void doDeleteTag(int tagId) {
        tagDictRepo.delete((long) tagId);
    }

    @Transactional
    public int addResource(RequestAddResource payload) {
        MainResource resource = new MainResource();
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

    public List<MainResourceContent> getResourceContent(int resourceId) {
        return resourceContentRepo.findByResourceId(resourceId);
    }

    @Transactional
    public void deleteResource(int resourceId) {
        resourceRepo.delete(resourceId);
        resourceContentRepo.deleteByResourceId(resourceId);
        resourceUserRepo.deleteByResourceId(resourceId);
        resourceUserTagRepo.deleteByResourceId(resourceId);
    }

    public List<Integer> getResourceUserTags(int resourceId, int userId){
        List<MainResourceUserTagR> r = resourceUserTagRepo.findByResourceIdAndUserId(resourceId, userId);
        List<Integer> ids = new ArrayList<>();
        for(MainResourceUserTagR tag: r){
            ids.add(tag.getTagId());
        }
        return ids;
    }
}
