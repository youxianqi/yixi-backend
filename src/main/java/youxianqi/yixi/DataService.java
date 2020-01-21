package youxianqi.yixi;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import youxianqi.yixi.model.*;
import youxianqi.yixi.reqres.RequestResourceList;
import youxianqi.yixi.reqres.RequestUserAction;

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

    public void init() {
    }

    public void start() {
        MainResourceUserTagR r = resourceUserTagRepo.findOneByResourceIdAndUserIdAndTagId(5,1, 403);
        logger.info(r.toString());
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
        Map<Short, List<MainTagDict>> tagsPerResType =
                allTags.stream().collect(Collectors.groupingBy(MainTagDict::getTagResourceType));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Short resType : tagsPerResType.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("resourceType", resType);
            map.put("tags", tagsPerResType.get(resType));
            result.add(map);
        }
        return result;
    }

    public List<CustomResource> getResourceList(RequestResourceList params) {
        return resourceRepo.getResourceList(params.getKtreeIds(), params.getResourceType(),
                params.getResourceStatus(), params.getResourceAccessType());
    }
    public List<CustomResource> getResourceListByOwner(RequestResourceList params) {
        return resourceRepo.getResourceListByOwner(params.getKtreeIds(), params.getResourceType(),
                params.getResourceStatus(), params.getResourceAccessType(), params.getOwnerUserId());
    }
    public List<CustomResource> getResourceListByTags(RequestResourceList params) {
        return resourceRepo.getResourceListByTags(params.getKtreeIds(), params.getResourceType(),
                params.getResourceStatus(), params.getResourceAccessType(), params.getTagIds());
    }
    public List<CustomResource> getResourceListByFav(RequestResourceList params) {
        return resourceRepo.getResourceListByFav(params.getKtreeIds(), params.getResourceType(),
                params.getResourceStatus(), params.getResourceAccessType(), params.getFavUserId());
    }
    /*
ActionType,1,buy,购买 -- todo
ActionType,2,view,观看
ActionType,3,tag,打标签
ActionType,4,like,赞
ActionType,5,fav,收藏
ActionType,6,comment,评论
     */
    public void doUserAction(RequestUserAction params) {
        if (params.isAddNotDelete()) {
            if (params.getActionType() == 2
                    || params.getActionType() == 4
                    || params.getActionType() == 5
                    || params.getActionType() == 6) {
                MainResourceUserR existed = resourceUserRepo.findOneByResourceIdAndUserId(params.getResourceId(), params.getUserId());
                if (existed == null) {
                    existed = new MainResourceUserR();
                    existed.setResourceId(params.getResourceId());
                    existed.setUserId(params.getUserId());
                }
                if (params.getActionType() == 2){
                    existed.setViews(existed.getViews() + 1);
                }
                if (params.getActionType() == 4){
                    existed.setLikes(existed.getLikes() + 1);
                }
                if (params.getActionType() == 5){
                    existed.setHasFaved(1);
                }
                if (params.getActionType() == 6){
                    existed.setComment(params.getComment());
                    existed.setCommentToUserid(params.getCommentToUserId());
                }
                resourceUserRepo.save(existed);
                return;
            }
            if (params.getActionType() == 3) {
                MainResourceUserTagR existed = resourceUserTagRepo.findOneByResourceIdAndUserIdAndTagId(
                        params.getResourceId(),params.getUserId(),params.getTagId());
                if (existed == null) {
                    existed = new MainResourceUserTagR();
                    existed.setResourceId(params.getResourceId());
                    existed.setUserId(params.getUserId());
                    existed.setTagId(params.getTagId());
                    resourceUserTagRepo.save(existed);
                    return;
                }
            }
        }
        else {
            if (params.getActionType() == 2
                    || params.getActionType() == 4
                    || params.getActionType() == 5
                    || params.getActionType() == 6) {
                MainResourceUserR existed = resourceUserRepo.findOneByResourceIdAndUserId(params.getResourceId(), params.getUserId());
                if (existed == null) {
                    existed = new MainResourceUserR();
                    existed.setResourceId(params.getResourceId());
                    existed.setUserId(params.getUserId());
                }
                if (params.getActionType() == 2){
                    if (existed.getViews() > 0) existed.setViews(existed.getViews() - 1);
                }
                if (params.getActionType() == 4){
                    if (existed.getLikes() > 0) existed.setViews(existed.getLikes() - 1);
                }
                if (params.getActionType() == 5){
                    existed.setHasFaved(0);
                }
                if (params.getActionType() == 6){
                    existed.setComment("");
                    existed.setCommentToUserid(0);
                }
                resourceUserRepo.save(existed);
                return;
            }
            if (params.getActionType() == 3) {
                MainResourceUserTagR existed = resourceUserTagRepo.findOneByResourceIdAndUserIdAndTagId(
                        params.getResourceId(),params.getUserId(),params.getTagId());
                if (existed != null){
                    resourceUserTagRepo.delete(existed);
                }
            }
        }
    }
}
