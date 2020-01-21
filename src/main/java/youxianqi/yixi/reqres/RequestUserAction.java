package youxianqi.yixi.reqres;

import lombok.Data;

@Data
public class RequestUserAction {
    private boolean isAddNotDelete;
    private int resourceId;
    private int userId;
    private int actionType;
    private int tagId;
    private String comment;
    private int commentToUserId;
}
