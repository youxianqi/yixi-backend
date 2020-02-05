package youxianqi.yixi.oss;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import youxianqi.yixi.utils.ExceptionUtil;
import org.apache.commons.io.IOUtils;
import youxianqi.yixi.utils.ResponseUtil;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/oss")
@Component
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private static String CALLBACK;

    @Autowired
    private OssConfig config;

    private OSSClient ossClient;
    private String ossPublicKey;

    public void init() {
        String template = "{\"callbackUrl\":\"%s\",\"callbackHost\":\"%s\",\"callbackBody\":\"%s\",\"callbackBodyType\":\"%s\"}";
        CALLBACK = String.format(template, config.getOssCallbackUrl(), config.getOssCallbackHost(),
                StringEscapeUtils.escapeJava(config.getOssCallbackBody()), config.getOssCallbackType());
        ossClient = new OSSClient(config.getOssEndpoint(), config.getOssAccessId(), config.getOssAccessKey());
        try {
            ossPublicKey = loadOssPublicKey();
        } catch (IOException ioe) {
            logger.warn("Failed to init OSS public key", ioe);
        }
    }

    @PostMapping(value = "/post_policy")
    public ResponseEntity<ResponsePostPolicy> postPolicy(@RequestBody Map<String, Object> payload) {
        try {
            String dir = "user-dir/"; // FIXME
            if (payload.containsKey("uploadDir")) {
                String s = ((String)payload.get("uploadDir"));
                if (StringUtils.isNotEmpty(s.trim())) {
                    dir = s.trim();
                }
            }
            /* see: https://ak-console.aliyun.com/#/accesskey */
            /* see: https://oss.console.aliyun.com/index */
            String host = "http://" + config.getOssBucket() + "." + config.getOssEndpoint();
            long expirationTick = System.currentTimeMillis() + config.getOssExpirationSeconds() * 1000;
            Date expiration = new Date(expirationTick);
            PolicyConditions policyConditions = new PolicyConditions();
            policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
            String encodedPostPolicy = BinaryUtil.toBase64String(postPolicy.getBytes(StandardCharsets.UTF_8));
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            String encodedCallback = BinaryUtil.toBase64String(CALLBACK.getBytes(StandardCharsets.UTF_8));
            ResponsePostPolicy response = new ResponsePostPolicy();
            response.setAccessId(config.getOssAccessId());
            response.setPolicy(encodedPostPolicy);
            response.setSignature(postSignature);
            response.setCallback(encodedCallback);
            response.setHost(host);
            response.setDir(dir);
            response.setExpires((int) (expirationTick / 1000));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
            return ResponseEntity.ok(new ResponsePostPolicy());
        }
    }

    /*
     * Use raw string, since we want to check signature.
     * Not "application/json", but "application/x-www-form-urlencoded" is used since
     * aliyun returns INVALID json response we cannot parse.
     */
    @PostMapping(value = "/callback")
    public ResponseEntity<Map<String, Object>> callback(HttpServletRequest contextRequest) {
        try {
            logger.info("object: {}", contextRequest.getParameter("object"));
            logger.info("size: {}", contextRequest.getParameter("size"));
            logger.info("mimeType: {}", contextRequest.getParameter("mimeType"));
            logger.info("imageHeight: {}", contextRequest.getParameter("imageHeight"));
            logger.info("imageWidth: {}", contextRequest.getParameter("imageWidth"));
        } catch (Exception e) {
        }
        return ResponseEntity.ok(ResponseUtil.success());
    }

    private String loadOssPublicKey() throws IOException {
        return loadOssPublicKey("https://gosspublic.alicdn.com/callback_pub_key_v1.pem");
    }

    private String loadOssPublicKey(String address) throws IOException {
        if (!address.startsWith("http://gosspublic.alicdn.com/")
                && !address.startsWith("https://gosspublic.alicdn.com/")) {
            logger.warn("Address of public key must be a oss address");
            return null;
        }
        String content = IOUtils.toString(new URL(address));
        content = content.replace("-----BEGIN PUBLIC KEY-----", "");
        content = content.replace("-----END PUBLIC KEY-----", "");
        content = content.trim();
        logger.info("Got content of public key: " + content);
        return content;
    }
}
