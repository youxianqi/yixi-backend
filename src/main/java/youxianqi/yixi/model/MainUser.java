package youxianqi.yixi.model;

import java.sql.*;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "youxianqi.yixi.model.MainUser")
@Table(name = "main_user")
public class MainUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"user_id\"", nullable = false)
  private Integer userId;
  @Column(name = "\"user_name\"", nullable = false)
  private String userName;
  @Column(name = "\"password\"", nullable = false)
  private String password;
  /**
   * SexType,1,female,女
   * SexType,2,male,男
   */
  @Column(name = "\"sex_type\"", nullable = false)
  private Short sexType;
  /**
   * UserStatus,1,invisible,隐藏
   * UserStatus,2,valid,valid
   * UserStatus,3,disabled,已失效
   */
  @Column(name = "\"user_status\"", nullable = false)
  private Short userStatus;
  @Column(name = "\"user_img\"", nullable = false)
  private String userImg;
  @Column(name = "\"local_update_time\"", nullable = false)
  private Timestamp localUpdateTime;
}