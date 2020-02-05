package youxianqi.yixi.reqres;

import lombok.Data;

@Data
public class RequestAddResource {
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
