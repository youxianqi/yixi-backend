package youxianqi.yixi.reqres;

import lombok.Data;

@Data
public class RequestResourceList {
    private String ktreeIds;
    private Integer resourceType;
    private Integer resourceStatus;
    private Integer resourceAccessType;
    private Integer orderByType;
    private Integer orderByDirection;
    private Integer offset;
    private Integer limit;
    private Integer ownerUserId;
    private String tagIds;
    private Integer favUserId;
}
