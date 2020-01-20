package youxianqi.yixi;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import youxianqi.yixi.generated.CustomEntity;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableScheduling
@RestController
@RequestMapping("/api")
@Component
public class MainService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    QueryRepo queryRepo;

    public void init() {
    }

    public void start() {
        List<CustomEntity> t = queryRepo.findList1(1);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object row : t) {
            Object[] rowArray = (Object[]) row;
            Map<String, Object> mapArr = new HashMap<String, Object>();
            mapArr.put("id", rowArray[0]);
            mapArr.put("title", rowArray[1]);
            mapArr.put("count", rowArray[2]);
            mapArr.put("flag", rowArray[3]);
            result.add(mapArr);
        }
        logger.info(result.toString());
    }

    @Scheduled(cron = "0 10 0 * * ?")
    public void dailyReset() {
    }

    @PostMapping(value = "/serverList")
    public ResponseEntity<Map<String, Object>> serverList() {

        List<Map<String, String>> serverList = new ArrayList<>();
        Map<String, String> server = new HashMap<>();
        server.put("IP", "172.16.3.113:8000");
        server.put("appId", "cdh.quickfix_m");
        serverList.add(server);

        Map<String, String> server2 = new HashMap<>();
        server2.put("IP", "172.16.3.112:8000");
        server2.put("appId", "cdh.quickfix_S");
        serverList.add(server2);

        return ResponseEntity.ok(success(serverList));
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> ret = new HashMap<>();
        ret.put("status", "0");
        ret.put("data", data);
        return ret;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        String username="", passwordMd5="";
        for(String key : payload.keySet()) {
            if (key.equals("username")) {
                username = (String) payload.get(key);
            }
            if (key.equals("password")) {
                passwordMd5 = (String) payload.get(key);
            }
        }
        List<Pair<String, String>> users = new ArrayList<>();
        users.add(Pair.of("小鱼说", "12345678"));

        for(Pair<String, String> pair : users) {
            if (username.equals(pair.getLeft())
                    && passwordMd5.equals(DigestUtils.md5Hex(pair.getRight()))) {
                return ResponseEntity.ok(success("ok"));
            }
        }
        return ResponseEntity.ok(success("failed"));
    }

    @PostMapping(value = "/changePwd")
    public ResponseEntity<Map<String, Object>> changePwd(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(success("ok"));
    }
}
