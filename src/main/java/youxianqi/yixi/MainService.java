package youxianqi.yixi;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import youxianqi.yixi.consts.ActionType;
import youxianqi.yixi.reqres.RequestAddTag;
import youxianqi.yixi.reqres.RequestResourceList;
import youxianqi.yixi.reqres.RequestUserAction;
import youxianqi.yixi.utils.ExceptionUtil;
import youxianqi.yixi.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;

@EnableScheduling
@RestController
@RequestMapping("/api")
@Component
public class MainService {
    static private Logger logger = LoggerFactory.getLogger(MainService.class);

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
        return ResponseEntity.ok(success(dataService.serverList()));
    }

    static private Map<String, Object> success() {
        return  response("ok", true);
    }

    static private Map<String, Object> success(Object data) {
        return response(data, true);
    }

    static private Map<String, Object> failed(Object data) {
        return response(data, false);
    }

    static private Map<String, Object> response(Object data, boolean success) {
        logger.info("response...{}", JsonUtil.objectToString(data, ""));
        Map<String, Object> ret = new HashMap<>();
        ret.put("status", success ? 200 : 201);
        ret.put("data", data);
        return ret;
    }

    static private String get(Map<String, Object> payload, String key) {
        return (String) payload.get(key);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("login...request: {}", payload.toString());
            int userId = dataService.verifyUser(get(payload, "username"), get(payload, "password"));
            if (userId > 0) {
                return ResponseEntity.ok(success(userId));
            } else {
                return ResponseEntity.ok(failed("用户名或密码错误"));
            }
        }
        catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/changePwd")
    public ResponseEntity<Map<String, Object>> changePwd(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("changePwd...request: {}", payload.toString());
            int userId = dataService.verifyUser(get(payload, "username"), get(payload, "oldPassword"));
            if (userId == 0) {
                return ResponseEntity.ok(failed("用户名或密码错误"));
            }
            dataService.changePwd(get(payload, "username"), get(payload, "password"));
            return ResponseEntity.ok(success());
        } catch (Exception e) {

            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/getTags")
    public ResponseEntity<Map<String, Object>> getTags() {
        try {
            logger.info("getTags...");
            return ResponseEntity.ok(success(dataService.getTags()));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/queryResourceList")
    public ResponseEntity<Map<String, Object>> getResourceList(@RequestBody RequestResourceList payload) {
        try {
            logger.info("queryResourceList...request: {}", payload.toString());
            return ResponseEntity.ok(success(dataService.getResourceList(payload)));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/addOneAction")
    public ResponseEntity<Map<String, Object>> addOneAction(@RequestBody RequestUserAction payload) {
        try {
            payload.setAddNotDelete(true);
            logger.info("addOneAction...request: {}", payload.toString());
            boolean changed = dataService.doUserAction(payload);
            if (changed) {
                return ResponseEntity.ok(success());
            }else {
                if (payload.getActionType() == ActionType.BUY.getValue()) {
                    return ResponseEntity.ok(failed("可能已经购买过了"));
                }
                if (payload.getActionType() == ActionType.LIKE.getValue()) {
                    return ResponseEntity.ok(failed("可能已经赞过了"));
                }
                if (payload.getActionType() == ActionType.TAG.getValue()) {
                    return ResponseEntity.ok(failed("可能已经标记过了"));
                }
                if (payload.getActionType() == ActionType.FAV.getValue()) {
                    return ResponseEntity.ok(failed("可能已经收藏过了"));
                }
                return ResponseEntity.ok(failed("可能已经执行过该操作了"));
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/deleteOneAction")
    public ResponseEntity<Map<String, Object>> deleteOneAction(@RequestBody RequestUserAction payload) {
        try {
            logger.info("deleteOneAction...request: {}", payload.toString());
            payload.setAddNotDelete(false);
            boolean changed = dataService.doUserAction(payload);
            if (changed) {
                return ResponseEntity.ok(success());
            }
            else {
                return ResponseEntity.ok(failed("取消未成功"));
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/addTag")
    public ResponseEntity<Map<String, Object>> addTag(@RequestBody RequestAddTag payload) {
        try {
            logger.info("addTag...request: {}", payload.toString());
            int existedId = dataService.doAddTag(payload);
            if (existedId > 0) {
                return ResponseEntity.ok(failed("已经存在该标签, id为" + existedId));
            }
            return ResponseEntity.ok(success());
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/deleteTag")
    public ResponseEntity<Map<String, Object>> deleteTag(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("deleteTag...request: {}", payload.toString());
            dataService.doDeleteTag(Integer.parseInt(get(payload, "tagId")));
            return ResponseEntity.ok(success());
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(failed(e.getMessage()));
        }
    }
}
