package youxianqi.yixi;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import youxianqi.yixi.consts.ActionType;
import youxianqi.yixi.model.MainResourceContent;
import youxianqi.yixi.oss.UploadController;
import youxianqi.yixi.reqres.RequestAddResource;
import youxianqi.yixi.reqres.RequestAddTag;
import youxianqi.yixi.reqres.RequestResourceList;
import youxianqi.yixi.reqres.RequestUserAction;
import youxianqi.yixi.utils.ExceptionUtil;
import youxianqi.yixi.utils.JsonUtil;
import youxianqi.yixi.utils.ResponseUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableScheduling
@RestController
@RequestMapping("/api")
@Component
public class MainController {
    static private Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    DataService dataService;

    @Autowired
    UploadController uploadController;

    public void init() {
        dataService.init();
        uploadController.init();
    }

    public void start() {
        String s = DigestUtils.md5Hex("12345678");
        dataService.start();
    }

    @PostMapping(value = "/serverList")
    public ResponseEntity<Map<String, Object>> serverList() {
        return ResponseEntity.ok(ResponseUtil.success(dataService.serverList()));
    }


    static private String get(Map<String, Object> payload, String key) {
        return (String) payload.get(key);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("login...post: {}", payload.toString());
            int userId = dataService.verifyUser(get(payload, "username"), get(payload, "password"));
            if (userId > 0) {
                return ResponseEntity.ok(ResponseUtil.success(userId));
            } else {
                return ResponseEntity.ok(ResponseUtil.failed("用户名或密码错误"));
            }
        }
        catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/changePwd")
    public ResponseEntity<Map<String, Object>> changePwd(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("changePwd...post: {}", payload.toString());
            int userId = dataService.verifyUser(get(payload, "username"), get(payload, "oldPassword"));
            if (userId == 0) {
                return ResponseEntity.ok(ResponseUtil.failed("用户名或密码错误"));
            }
            dataService.changePwd(get(payload, "username"), get(payload, "password"));
            return ResponseEntity.ok(ResponseUtil.success());
        } catch (Exception e) {

            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/getTags")
    public ResponseEntity<Map<String, Object>> getTags() {
        try {
            logger.info("getTags...");
            return ResponseEntity.ok(ResponseUtil.success(dataService.getTags()));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/queryResourceList")
    public ResponseEntity<Map<String, Object>> getResourceList(@RequestBody RequestResourceList payload) {
        try {
            logger.info("queryResourceList...post: {}", payload.toString());
            return ResponseEntity.ok(ResponseUtil.success(dataService.getResourceList(payload)));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/addOneAction")
    public ResponseEntity<Map<String, Object>> addOneAction(@RequestBody RequestUserAction payload) {
        try {
            payload.setAddNotDelete(true);
            logger.info("addOneAction...post: {}", payload.toString());
            boolean changed = dataService.doUserAction(payload);
            if (changed) {
                return ResponseEntity.ok(ResponseUtil.success());
            }else {
                if (payload.getActionType() == ActionType.BUY.getValue()) {
                    return ResponseEntity.ok(ResponseUtil.failed("可能已经购买过了"));
                }
                if (payload.getActionType() == ActionType.LIKE.getValue()) {
                    return ResponseEntity.ok(ResponseUtil.failed("可能已经赞过了"));
                }
                if (payload.getActionType() == ActionType.TAG.getValue()) {
                    return ResponseEntity.ok(ResponseUtil.failed("可能已经标记过了"));
                }
                if (payload.getActionType() == ActionType.FAV.getValue()) {
                    return ResponseEntity.ok(ResponseUtil.failed("可能已经收藏过了"));
                }
                return ResponseEntity.ok(ResponseUtil.failed("可能已经执行过该操作了"));
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/deleteOneAction")
    public ResponseEntity<Map<String, Object>> deleteOneAction(@RequestBody RequestUserAction payload) {
        try {
            logger.info("deleteOneAction...post: {}", payload.toString());
            payload.setAddNotDelete(false);
            boolean changed = dataService.doUserAction(payload);
            if (changed) {
                return ResponseEntity.ok(ResponseUtil.success());
            }
            else {
                return ResponseEntity.ok(ResponseUtil.failed("取消未成功"));
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/addTag")
    public ResponseEntity<Map<String, Object>> addTag(@RequestBody RequestAddTag payload) {
        try {
            logger.info("addTag...post: {}", payload.toString());
            int existedId = dataService.doAddTag(payload);
            if (existedId > 0) {
                return ResponseEntity.ok(ResponseUtil.failed("已经存在该标签, id为" + existedId));
            }
            return ResponseEntity.ok(ResponseUtil.success());
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/deleteTag")
    public ResponseEntity<Map<String, Object>> deleteTag(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("deleteTag...post: {}", payload.toString());
            dataService.doDeleteTag(Integer.parseInt(get(payload, "tagId")));
            return ResponseEntity.ok(ResponseUtil.success());
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/addResource")
    public ResponseEntity<Map<String, Object>> addResource(@RequestBody RequestAddResource payload) {
        try {
            logger.info("addResource...post: {}", payload.toString());
            int newId = dataService.addResource(payload);
            return ResponseEntity.ok(ResponseUtil.success(newId));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/getResourceContent")
    public ResponseEntity<Map<String, Object>> getResourceContent(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("getResourceContent...post: {}", payload.toString());
            List<MainResourceContent> r = dataService.getResourceContent(
                    Integer.parseInt(payload.get("resourceId").toString()));
            return ResponseEntity.ok(ResponseUtil.success(r));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }
    @PostMapping(value = "/deleteResource")
    public ResponseEntity<Map<String, Object>> deleteResource(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("getResourceContent...post: {}", payload.toString());
            dataService.deleteResource(Integer.parseInt(payload.get("resourceId").toString()));
            return ResponseEntity.ok(ResponseUtil.success());
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }
    @PostMapping(value = "/queryOneResource")
    public ResponseEntity<Map<String, Object>> queryOneResource(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("getResourceContent...post: {}", payload.toString());
            return ResponseEntity.ok(ResponseUtil.success(
                    dataService.queryOneResource(
                            Integer.parseInt(payload.get("userId").toString()),
                            Integer.parseInt(payload.get("resourceId").toString()))
            ));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }
    @PostMapping(value = "/getResourceUserTags")
    public ResponseEntity<Map<String, Object>> getResourceUserTags(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("getResourceContent...post: {}", payload.toString());
            List<Integer> tags = dataService.getResourceUserTags(
                    Integer.parseInt(payload.get("resourceId").toString()),
                    Integer.parseInt(payload.get("userId").toString()));
            return ResponseEntity.ok(ResponseUtil.success(tags));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/register/existUser")
    public ResponseEntity<Map<String, Object>> registerExistUser(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(ResponseUtil.success(dataService.existUser(
                    payload.get("userName").toString()
            )));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/register/existMobile")
    public ResponseEntity<Map<String, Object>> registerExistMobile(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(ResponseUtil.success(dataService.existMobile(
                    payload.get("mobile").toString()
            )));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    //width=120&height=40&verifySize=4&hash=
    @GetMapping(value = "/register/newCaptcha",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] newCaptcha(@RequestParam("width") Integer width,
                             @RequestParam("height") Integer height,
                             @RequestParam("verifySize") Integer verifySize,
                             @RequestParam("hash") String hash) {
        try {
            return dataService.newCaptcha(width, height, verifySize, hash);
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return null;
        }
    }

    @PostMapping(value = "/register/verifyCaptcha")
    public ResponseEntity<Map<String, Object>> verifyCaptcha(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(ResponseUtil.success(dataService.verifyCaptcha(
                    payload.get("hash").toString(),
                    payload.get("captcha").toString())));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/register/sendMobileVerifyCode")
    public ResponseEntity<Map<String, Object>> sendMobileVerifycode(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(ResponseUtil.success(dataService.sendMobileVerifycode(
                    payload.get("hash").toString(),
                    payload.get("captcha").toString(),
                    payload.get("mobile").toString())
                    ));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/register/registerUser")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(ResponseUtil.success(dataService.registerUser(
                    payload.get("userName").toString(),
                    payload.get("hash").toString(),
                    payload.get("mobile").toString(),
                    payload.get("mobileCode").toString(),
                    payload.get("password").toString()
                    )));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }

    @PostMapping(value = "/register/resetPassword")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(ResponseUtil.success(dataService.resetPassword(
                    payload.get("mobile").toString()
            )));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(ResponseUtil.failed(e.getMessage()));
        }
    }
}
