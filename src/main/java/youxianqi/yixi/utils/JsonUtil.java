package youxianqi.yixi.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
    public static ObjectMapper mapper() {
        return mapper;
    }

    public static String objectToString(Object object, String defaultValue) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("objectToString(), unexpected error : {}", ExceptionUtil.getExceptionStack(e));
        }
        return defaultValue;
    }
    public static <T> T stringToObject(String str, Class<T> clazz) {
        try {
            return mapper.readValue(str, clazz);
        } catch (IOException e) {
            logger.error("stringToMap(), string: {}, unexpected error : {}", str, ExceptionUtil.getExceptionStack(e));
        }
        return null;
    }
    public static byte[] objectToBytes(Object object) {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("objectToBytes(), unexpected error : {}", ExceptionUtil.getExceptionStack(e));
        }
        return null;
    }
    public static <T> T bytesToObject(byte[] bytes, Class<T> clazz) {
        try {
            return mapper.readValue(bytes, clazz);
        } catch (IOException e) {
            logger.error("bytesToObject(), unexpected error : {}", ExceptionUtil.getExceptionStack(e));
        }
        return null;
    }
}
