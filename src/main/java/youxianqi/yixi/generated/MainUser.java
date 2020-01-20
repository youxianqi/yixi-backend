package youxianqi.yixi.generated;

import java.sql.*;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "main_user")
@Table(name = "main_user")
public class MainUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"UserId\"", nullable = false)
  private Integer userid;
  @Column(name = "\"UserName\"", nullable = false)
  private String username;
  @Column(name = "\"Password\"", nullable = false)
  private String password;
  /**
   * SexType,1,female,女
   * SexType,2,male,男
   */
  @Column(name = "\"SexType\"", nullable = false)
  private Short sextype;
  /**
   * UserStatus,1,invisible,隐藏
   * UserStatus,2,valid,valid
   * UserStatus,3,disabled,已失效
   */
  @Column(name = "\"UserStatus\"", nullable = false)
  private Short userstatus;
  @Column(name = "\"UserImg\"", nullable = false)
  private String userimg;
  @Column(name = "\"LocalTime\"", nullable = false)
  private Timestamp localtime;
}