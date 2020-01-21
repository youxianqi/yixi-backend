package youxianqi.yixi;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import youxianqi.yixi.reqres.RequestResourceList;
import youxianqi.yixi.utils.ExceptionUtil;

import java.util.HashMap;
import java.util.Map;

@EnableScheduling
@RestController
@RequestMapping("/api")
@Component
public class MainService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DataService dataService;

    public void init() {
        dataService.init();
    }

    public void start() {
        String s = DigestUtils.md5Hex("12345678");
        dataService.start();
    }

    @PostMapping(value = "/serverList")
    public ResponseEntity<Map<String, Object>> serverList() {
        return ResponseEntity.ok(response(dataService.serverList()));
    }

    static private Map<String, Object> success() {
        return  response("ok");
    }
    static private Map<String, Object> response(Object data) {
        Map<String, Object> ret = new HashMap<>();
        ret.put("status", 200);
        ret.put("data", data);
        return ret;
    }

    static private String get(Map<String, Object> payload, String key) {
        return (String) payload.get(key);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        try {
            if (dataService.verifyUser(get(payload, "username"), get(payload, "password"))) {
                return ResponseEntity.ok(success());
            } else {
                return ResponseEntity.ok(response("用户名或密码错误"));
            }
        }
        catch (Exception e) {
            logger.error("login...req: {}", payload.toString());
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(response(e.getMessage()));
        }
    }

    @PostMapping(value = "/changePwd")
    public ResponseEntity<Map<String, Object>> changePwd(@RequestBody Map<String, Object> payload) {
        try {
            if (!dataService.verifyUser(get(payload, "username"), get(payload, "oldPassword"))) {
                return ResponseEntity.ok(response("用户名或密码错误"));
            }
            dataService.changePwd(get(payload, "username"), get(payload, "password"));
            return ResponseEntity.ok(success());
        } catch (Exception e) {
            logger.error("changePwd...req: {}", payload.toString());
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(response(e.getMessage()));
        }
    }

    @PostMapping(value = "/getTags")
    public ResponseEntity<Map<String, Object>> getTags() {
        try {
            return ResponseEntity.ok(response(dataService.getTags()));
        } catch (Exception e) {
            logger.error("getTags...");
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(response(e.getMessage()));
        }
    }

    @PostMapping(value = "/getResourceList")
    public ResponseEntity<Map<String, Object>> getResourceList(@RequestBody RequestResourceList payload) {
        try {
            return ResponseEntity.ok(response(dataService.getResourceList(payload)));
        } catch (Exception e) {
            logger.error("getResourceList...req: {}", payload.toString());
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(response(e.getMessage()));
        }
    }
}
