package youxianqi.yixi.model;

import java.util.Date;

public interface CustomResource {
    int getResourceId();
    int getKtreeId();
    int getResourceType();
    int getResourceStatus();
    int getResourceAccessType();
    Date getDataTime();
    String getTitle();
    String getDescription();
    String getThumbnailUrl();
    String getContent();
    int getContentType();
    int getOwnerUserId();
    String getOwnerUserName();
    int getOwnerUserSexType();
    String getOwnerUserImg();
    int getViews();
    int getLikes();
    int getFavs();
    String getTagsJson();
    Date getLocalUpdateTime();
}
