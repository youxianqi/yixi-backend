package youxianqi.yixi.model;

import java.sql.*;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity(name = "youxianqi.yixi.model.MainResource")
@Table(name = "main_resource")
public class MainResource {

  /**
   * primary key
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"resource_id\"", nullable = false)
  private Integer resourceId;
  @Column(name = "\"ktree_id\"", nullable = false)
  private String ktreeId;
  /**
   * ResourceType,1,course,课程
   * ResourceType,2,exercise,习题
   * ResourceType,3,presentation,成果展示
   * ResourceType,4,entertainment,娱乐
   * ResourceType,5,social,社交
   */
  @Column(name = "\"resource_type\"", nullable = false)
  private Byte resourceType;
  /**
   * ResourceStatus,1,drafting,未发布
   * ResourceStatus,2,published,已发布
   * ResourceStatus,3,disabled,已失效
   */
  @Column(name = "\"resource_status\"", nullable = false)
  private Byte resourceStatus;
  /**
   * ResourceAccessType,1,public
   * ResourceAccessType,2,user
   * ResourceAccessType,3,paidUser
   */
  @Column(name = "\"resource_access_type\"", nullable = false)
  private Byte resourceAccessType;
  @Column(name = "\"content_title\"", nullable = false)
  private String contentTitle;
  @Column(name = "\"content_desc\"", nullable = false)
  private String contentDesc;
  @Column(name = "\"content_time\"", nullable = false)
  private Timestamp contentTime;
  @Column(name = "\"content_thumbnail\"", nullable = false)
  private String contentThumbnail="";
  @Column(name = "\"content_count\"", nullable = false)
  private Byte contentCount;
  /**
   * ContentType,1,video,视频
   * ContentType,2,audio,音频
   * ContentType,3,image,图像
   * ContentType,4,text,文本
   * ContentType,5,exercise_item,题目
   * ContentType,6,parent,组合
   */
  @Column(name = "\"content_media_type\"", nullable = false)
  private Byte contentMediaType;
  @Column(name = "\"parent_id_d\"", nullable = false)
  private Integer parentIdD=0;
  @Column(name = "\"owner_user_id\"", nullable = false)
  private Integer ownerUserId;
  @Column(name = "\"views_d\"", nullable = false)
  private Integer viewsD=0;
  @Column(name = "\"likes_d\"", nullable = false)
  private Integer likesD=0;
  @Column(name = "\"favs_d\"", nullable = false)
  private Integer favsD=0;
  @Column(name = "\"tags_json_d\"", nullable = true)
  private String tagsJsonD;
  @Column(name = "\"local_update_time\"", nullable = false)
  @UpdateTimestamp
  private Timestamp localUpdateTime;
}