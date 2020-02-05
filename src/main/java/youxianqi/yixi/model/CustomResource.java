package youxianqi.yixi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

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
    private  Date contentTime;
    private  String contentTitle;
    private  String contentDesc;
    private  String contentThumbnail;
    private  int contentMediaType;
    private  int ownerUserId;
    private  String ownerUserName;
    private  int ownerUserSexType;
    private  String ownerUserImg;
    private  int views;
    private  int likes;
    private  int favs;
    private  String tagsJson;
    @JsonFormat(pattern="yyyy-MM-dd hh:mm")
    private  Date localUpdateTime;
}