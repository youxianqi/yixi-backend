package youxianqi.yixi.model;

import java.sql.*;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "youxianqi.yixi.model.MainResourceUserR")
@Table(name = "main_resource_user_r")
public class MainResourceUserR {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"id\"", nullable = false)
  private Integer id;
  @Column(name = "\"resource_id\"", nullable = false)
  private Integer resourceId;
  @Column(name = "\"user_id\"", nullable = false)
  private Integer userId;
  @Column(name = "\"views\"", nullable = false)
  private Integer views;
  @Column(name = "\"likes\"", nullable = false)
  private Integer likes;
  @Column(name = "\"has_faved\"", nullable = false)
  private Integer hasFaved;
  @Column(name = "\"comment_to_userid\"", nullable = false)
  private Integer commentToUserid;
  @Column(name = "\"comment\"", nullable = false)
  private String comment;
  @Column(name = "\"local_update_time\"", nullable = false)
  private Timestamp localUpdateTime;
}