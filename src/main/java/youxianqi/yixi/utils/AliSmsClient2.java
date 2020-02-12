package youxianqi.yixi.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AliSmsClient2 {
    private static final Logger logger = LoggerFactory.getLogger(AliSmsClient2.class);

    String accessKey;
    String accessSecret;
    String verifyCodeTemplate;
    String resetPasswordTemplate;
    String regionId;
    String signName;

    DefaultProfile profile;
    IAcsClient client;

    public void init(String accessKey,
                         String accessSecret,
                         String verifyCodeTemplate,
                         String resetPwdTemplate,
                         String regionId,
                         String signName) {
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
        this.verifyCodeTemplate = verifyCodeTemplate;
        this.resetPasswordTemplate = resetPwdTemplate;
        this.regionId = regionId;
        this.signName = signName;

        profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        client = new DefaultAcsClient(profile);
    }

    public Pair<Boolean, String> sendVerify(String mobile, String verifyCode){
        return send(true, mobile, verifyCode);
    }

    public Pair<Boolean, String> sendResetPassword(String mobile, String password){
        return send(false, mobile, password);
    }

    private Pair<Boolean, String> send(boolean isVerifyCode, String mobile, String codeOrPassword) {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        String paramName = isVerifyCode ? "code" : "password";

        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("TemplateCode", isVerifyCode ? verifyCodeTemplate : resetPasswordTemplate);
        request.putQueryParameter("TemplateParam", String.format("{\"%s\":\"%s\"}", paramName, codeOrPassword));

        try {
            CommonResponse response = client.getCommonResponse(request);
            logger.info("AliSmsClient2.send...{}", response.getData());
            /*
            {"Message":"OK","RequestId":"5F06BF01-12D9-413B-A700-F4AA195E10C9","BizId":"304308681497013366^0","Code":"OK"}
            */
            AliSmsResult result = JsonUtil.stringToObject(response.getData(), AliSmsResult.class);
            return Pair.of(result.getCode().equals("OK"), result.getMessage());
        } catch (Exception e) {
            logger.error("AliSmsClient2.send error...request...{}", ExceptionUtil.getExceptionStack(e));
            logger.error("AliSmsClient2.send unexcepted...{}", ExceptionUtil.getExceptionStack(e));
            return null;
        }
    }
    static public void main(String[] args){
        AliSmsClient2 smsClient = new AliSmsClient2();
        smsClient.init("LTAI4FvtNMJVafQy9hypvzD4",
                "LIWyoxT4xPtdjPIZT2T7Y0SR5MRX8R",
                "SMS_183247436",
                "SMS_183242435",
                "cn-shanghai",
                "学习小站");
        Pair<Boolean, String> b = smsClient.sendResetPassword("18616699733",
                CaptchaUtil.generateVerifyCode(8));
        logger.info(b.toString());
    }
}
