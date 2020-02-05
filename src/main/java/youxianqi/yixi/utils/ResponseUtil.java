package youxianqi.yixi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zushun on 2020/1/27.
 */
public class ResponseUtil {
    static private Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

    static public Map<String, Object> success() {
        return  response("ok", true);
    }

    static public Map<String, Object> success(Object data) {
        return response(data, true);
    }

    static public Map<String, Object> failed(Object data) {
        return response(data, false);
    }

    static private Map<String, Object> response(Object data, boolean success) {
        logger.info("response...{}", JsonUtil.objectToString(data, ""));
        Map<String, Object> ret = new HashMap<>();
        ret.put("status", success ? 200 : 201);
        ret.put("data", data);
        return ret;
    }
}
