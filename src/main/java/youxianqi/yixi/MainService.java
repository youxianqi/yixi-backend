package youxianqi.yixi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

    public void init() {
    }

    public void start() {
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
        if (username.equals("小鱼说")
                && passwordMd5.equals(DigestUtils.md5Digest("123456".getBytes(StandardCharsets.UTF_8)))) {
            return ResponseEntity.ok(success("ok"));
        }
        return ResponseEntity.ok(success("failed"));
    }
}
