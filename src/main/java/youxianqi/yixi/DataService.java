package youxianqi.yixi;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import youxianqi.yixi.consts.ActionType;
import youxianqi.yixi.consts.BoolType;
import youxianqi.yixi.model.*;
import youxianqi.yixi.reqres.RequestAddTag;
import youxianqi.yixi.reqres.RequestResourceList;
import youxianqi.yixi.reqres.RequestUserAction;
import youxianqi.yixi.utils.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResourceRepo resourceRepo;

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

        for(Pair<String, String> pair : users) {
            if (username.equals(pair.getLeft())
                    && password.equals(DigestUtils.md5Hex(pair.getRight()))) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyUser(String username, String password) {
        List<MainUser> users = userRepo.findAll();
        for(MainUser user : users) {
            if (username.equals(user.getUserName())
                    && password.equals(user.getPassword())) {
                return true;
            }
        }
        return false;
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

    private static void increaseTagCount(MainResource resource, int tagId){
        Map<Integer, Integer> tags = null;
        int tagCount = 0;
        if (StringUtils.isEmpty(resource.getTagsJsonD())) {
            tags = new HashMap<>();
        }
        else {
            tags = JsonUtil.stringToObject(resource.getTagsJsonD(), HashMap.class);
            if (tags.containsKey(tagId)) {
                tagCount = tags.get(tagId);
            }
        }
        tags.put(tagId, tagCount + 1);
        resource.setTagsJsonD(JsonUtil.objectToString(tags, ""));
    }

    private static void decreaseTagCount(MainResource resource, int tagId){
        Map<Integer, Integer> tags = null;
        if (StringUtils.isEmpty(resource.getTagsJsonD())) {
            return;
        }
        tags = JsonUtil.stringToObject(resource.getTagsJsonD(), HashMap.class);
        if (!tags.containsKey(tagId)) {
            return;
        }
        int tagCount = tags.get(tagId);
        if (tagCount > 0) {
            tags.put(tagId, tagCount - 1);
        }
        resource.setTagsJsonD(JsonUtil.objectToString(tags, ""));
    }

    @Transactional
    public void doUserAction(RequestUserAction params) {
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
                }
                if (params.getActionType() == ActionType.VIEW.getValue()){
                    existed.setViews(existed.getViews() + 1);
                    resource.setViewsD(resource.getViewsD() + 1);
                }
                if (params.getActionType() == ActionType.LIKE.getValue()){
                    if (existed.getHasLiked() == BoolType.FALSE.getValue()) {
                        resource.setLikesD(resource.getLikesD() + 1);
                    }
                    existed.setHasLiked(BoolType.TRUE.getValue());
                }
                if (params.getActionType() == ActionType.FAV.getValue()){
                    if (existed.getHasFaved() == BoolType.FALSE.getValue()) {
                        resource.setFavsD(resource.getFavsD() + 1);
                    }
                    existed.setHasFaved(BoolType.TRUE.getValue());
                }
                if (params.getActionType() == ActionType.COMMENT.getValue()){
                    existed.setComment(params.getComment());
                    existed.setCommentToUserid(params.getCommentToUserId());
                }
                resourceUserRepo.save(existed);
                resourceRepo.save(resource);
                return;
            }
            if (params.getActionType() == ActionType.TAG.getValue()) {
                MainResourceUserTagR existed = resourceUserTagRepo.findOneByResourceIdAndUserIdAndTagId(
                        params.getResourceId(),params.getUserId(),params.getTagId());
                if (existed == null) {
                    existed = new MainResourceUserTagR();
                    existed.setResourceId(params.getResourceId());
                    existed.setUserId(params.getUserId());
                    existed.setTagId(params.getTagId());
                    resourceUserTagRepo.save(existed);
                    increaseTagCount(resource, params.getTagId());
                    resourceRepo.save(resource);
                    return;
                }
            }
        }
        else {
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
                if (params.getActionType() == ActionType.VIEW.getValue()){
                    if (existed.getViews() > 0) existed.setViews(existed.getViews() - 1);
                    if (resource.getViewsD() > 0) resource.setViewsD(resource.getViewsD() - 1);
                }
                if (params.getActionType() == ActionType.LIKE.getValue()){
                    existed.setHasLiked(BoolType.FALSE.getValue());
                    if (resource.getLikesD() > 0) resource.setLikesD(resource.getLikesD() - 1);
                }
                if (params.getActionType() == ActionType.FAV.getValue()){
                    existed.setHasFaved(BoolType.FALSE.getValue());
                    if (resource.getFavsD() > 0) resource.setFavsD(resource.getFavsD() - 1);
                }
                if (params.getActionType() == ActionType.COMMENT.getValue()){
                    existed.setComment("");
                    existed.setCommentToUserid(0);
                }
                resourceUserRepo.save(existed);
                resourceRepo.save(resource);
                return;
            }
            if (params.getActionType() == ActionType.TAG.getValue()) {
                MainResourceUserTagR existed = resourceUserTagRepo.findOneByResourceIdAndUserIdAndTagId(
                        params.getResourceId(),params.getUserId(),params.getTagId());
                if (existed != null){
                    resourceUserTagRepo.delete(existed);
                    decreaseTagCount(resource, params.getTagId());
                    resourceRepo.save(resource);
                    return;
                }
            }
        }
    }

    public int doAddTag(RequestAddTag params) {
        MainTagDict existed = tagDictRepo.findByTagNameAndTagResourceType(params.getTagName(), (byte)params.getTagResourceType());
        if (existed != null) {
            return existed.getTagId();
        }
        MainTagDict tag = new MainTagDict();
        tag.setTagName(params.getTagName());
        tag.setTagResourceType((byte)params.getTagResourceType());
        tagDictRepo.save(tag);
        return 0;
    }

    public void doDeleteTag(int tagId) {
        tagDictRepo.delete((long)tagId);
    }
}
