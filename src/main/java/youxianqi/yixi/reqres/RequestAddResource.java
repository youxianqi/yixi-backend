package youxianqi.yixi.reqres;

import lombok.Data;

@Data
public class RequestAddResource {
    private Integer resourceId; // is UPDATE when present
    private int userid;
    private int resourceType;
    private int ktreeId;
    private String contentTitle;
    private String contentDesc;
    private int contentMediaType;
    private String contentTime;
    private String contentThumbnail;
    private int contentCount;
    private String mediaUrlListJson;
    private String textListJson;
    private String eiListJson;
}
