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

    public void init() {
    }

    public void start() {

        List<CustomUser> t = resourceRepo.getCustomUserList("1,2,3");
        logger.info(t.toString());
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
}
