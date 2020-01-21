package youxianqi.yixi.reqres;

import lombok.Data;

@Data
public class RequestResourceList {
    private String ktreeIds;
    private int resourceType;
    private int resourceStatus;
    private int resourceAccessType;
    private int ownerUserId;
    private String tagIds;
    private int favUserId;
}
