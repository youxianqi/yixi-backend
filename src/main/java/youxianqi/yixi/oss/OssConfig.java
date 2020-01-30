package youxianqi.yixi.oss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OssConfig {
    @Value("${application.oss.accessId}")
    private String ossAccessId;

    @Value("${application.oss.accessKey}")
    private String ossAccessKey;

    @Value("${application.oss.bucket}")
    private String ossBucket;

    @Value("${application.oss.endpoint}")
    private String ossEndpoint;

    @Value("${application.oss.callbackUrl}")
    private String ossCallbackUrl;

    @Value("${application.oss.callbackHost}")
    private String ossCallbackHost;

    @Value("${application.oss.callbackBody}")
    private String ossCallbackBody;

    @Value("${application.oss.callbackType}")
    private String ossCallbackType;

    @Value("${application.oss.expirationSeconds}")
    private int ossExpirationSeconds;

    public String getOssAccessId() {
        return ossAccessId;
    }

    public String getOssAccessKey() {
        return ossAccessKey;
    }

    public String getOssBucket() {
        return ossBucket;
    }

    public String getOssEndpoint() {
        return ossEndpoint;
    }

    public String getOssCallbackUrl() {
        return ossCallbackUrl;
    }

    public String getOssCallbackHost() {
        return ossCallbackHost;
    }

    public String getOssCallbackBody() {
        return ossCallbackBody;
    }

    public String getOssCallbackType() {
        return ossCallbackType;
    }

    public int getOssExpirationSeconds() {
        return ossExpirationSeconds;
    }
}
