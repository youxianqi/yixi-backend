package youxianqi.yixi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class AliSmsClient {
    private static final Logger logger = LoggerFactory.getLogger(AliSmsClient.class);

    private String serverUrl;
    private String accessKeyId;  /* APIID */
    private String accessSecret;  /* APIKEY */
    private String verifyCodeTemplate;
    private String findPasswordTemplate;
    private String regionId;
    private String signName;

    public AliSmsClient(String serverUrl,
                        String accessKeyId,
                        String accessSecret,
                        String verifyCodeTemplate,
                        String findPasswordTemplate,
                        String regionId,
                        String signName) {
        this.serverUrl = serverUrl;
        this.accessKeyId = accessKeyId;
        this.accessSecret = accessSecret;
        this.verifyCodeTemplate = verifyCodeTemplate;
        this.findPasswordTemplate = findPasswordTemplate;
        this.regionId = regionId;
        this.signName = signName;
    }

    public void close() {
    }

    public String sendVerify(String mobile, String verifyCode){
        return send(this.verifyCodeTemplate, mobile, "{\"code\":\"" + verifyCode + "\"}");
    }

    public String sendFindPassword(String mobile, String name, String password){
        return send(this.findPasswordTemplate, mobile, "{\"password\":\"" + password + "\"}");
    }

    public String send(String templateCode, String mobile, String templateParam) {
        try {
            String url = getUrl(templateCode, mobile, templateParam);
            RestClient rc = new RestClient();
            AliSmsResult result = rc.get(url, AliSmsResult.class);
            if (result.getCode().equals("OK")) {
                return result.getRequestId();
            } else {
                logger.warn("Error: " + result.toString());
            }
        } catch (Exception re) {
            logger.warn("Error sending sms", re);
        }
        return "";
    }

    private String getUrl(String templateCode, String mobile, String templateParam){
        try {
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));// 这里一定要设置GMT时区
            java.util.Map<String, String> paras = new java.util.HashMap<>();
            // 1. 系统参数
            paras.put("SignatureMethod", "HMAC-SHA1");
            paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
            paras.put("AccessKeyId", this.accessKeyId);
            paras.put("SignatureVersion", "1.0");
            paras.put("Timestamp", df.format(new java.util.Date()));
            paras.put("Format", "JSON");
            // 2. 业务API参数
            paras.put("Action", "SendSms");
            paras.put("Version", "2017-05-25");
            paras.put("RegionId", regionId);
            paras.put("PhoneNumbers", mobile);
            paras.put("SignName", signName);
            paras.put("TemplateParam", templateParam);
            paras.put("TemplateCode", templateCode);
            paras.put("OutId", "123");
            // 3. 去除签名关键字Key
            if (paras.containsKey("Signature"))
                paras.remove("Signature");
            // 4. 参数KEY排序
            java.util.TreeMap<String, String> sortParas = new java.util.TreeMap<String, String>();
            sortParas.putAll(paras);
            // 5. 构造待签名的字符串
            java.util.Iterator<String> it = sortParas.keySet().iterator();
            StringBuilder sortQueryStringTmp = new StringBuilder();
            while (it.hasNext()) {
                String key = it.next();
                sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
            }
            String sortedQueryString = sortQueryStringTmp.substring(1);// 去除第一个多余的&符号
            StringBuilder stringToSign = new StringBuilder();
            stringToSign.append("GET").append("&");
            stringToSign.append(specialUrlEncode("/")).append("&");
            stringToSign.append(specialUrlEncode(sortedQueryString));
            String sign = sign(this.accessSecret + "&", stringToSign.toString());
            // 6. 签名最后也要做特殊URL编码
            String signature = specialUrlEncode(sign);
            // 最终打印出合法GET请求的URL
            return this.serverUrl + signature + sortQueryStringTmp;
        }
        catch (RuntimeException ex){
            logger.warn("Error generating sms GET url", ex);
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        AliSmsClient client= new AliSmsClient("http://dysmsapi.aliyuncs.com/?Signature=",
                "LTAIZKyzfN4bmjuY",
                "Rvbj5gIWMaJtBzItyVckkK9jxAEdB4",
                "SMS_86950101",
                "SMS_92760011",
                "cn-shanghai",
                "学习小站");
        client.sendVerify("13965767622", "999999");
        client.sendFindPassword("13965767622", "super", "12345678");
    }

    public static String specialUrlEncode(String value)  {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        }
        catch (UnsupportedEncodingException ex){
            logger.warn("smsAli specialUrlEncode failed, value = ", value);
        }
        return "";
    }

    public static String sign(String accessSecret, String stringToSign){
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return new sun.misc.BASE64Encoder().encode(signData);
        }
        catch (Exception ex){
            logger.warn("smsAli sign failed, value = ", stringToSign);
        }
        return "";
    }
}
