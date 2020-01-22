package youxianqi.yixi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
/*
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
    String getCommentsJson();
    Date getLocalUpdateTime();
}
*/

@Data
@Entity(name = "youxianqi.yixi.model.CustomResource")
public class CustomResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer resourceId;
    private  int ktreeId;
    private  int resourceType;
    private  int resourceStatus;
    private  int resourceAccessType;
    @JsonFormat(pattern="yyyy-MM-dd hh:mm")
    private  Date dataTime;
    private  String title;
    private  String description;
    private  String thumbnailUrl;
    private  String content;
    private  int contentType;
    private  int ownerUserId;
    private  String ownerUserName;
    private  int ownerUserSexType;
    private  String ownerUserImg;
    private  int views;
    private  int likes;
    private  int favs;
    private  String tagsJson;
    //private  String commentsJson;
    @JsonFormat(pattern="yyyy-MM-dd hh:mm")
    private  Date localUpdateTime;
}