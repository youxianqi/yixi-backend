package youxianqi.yixi.utils;

public class AliSmsResult{
    public String Message;
    public	String RequestId;
    public	String BizId;
    public	String Code;

    public String getMessage() {
        return Message;
    }

    public String getRequestId() {
        return RequestId;
    }

    public String getBizId() {
        return BizId;
    }

    public String getCode() {
        return Code;
    }

    @Override
    public String toString() {
        return String.format("requestId=%s code=%s msg=%s", RequestId, Code, Message);
    }
}