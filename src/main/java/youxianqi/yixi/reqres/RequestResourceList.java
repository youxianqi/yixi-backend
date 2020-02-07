package youxianqi.yixi.reqres;

import lombok.Data;

@Data
public class RequestResourceList {
    private Integer userId;
    private String ktreeIds;
    private Integer resourceType;
    private Integer resourceStatus;
    private Integer resourceAccessType;
    private Integer orderByType;
    private Integer orderByDirection;
    private Integer offset;
    private Integer limit;
    private String tagIds;
    private Integer ownerUserId;
    private Integer favUserId;
    private String searchKey;
}
