package youxianqi.yixi.model;

import java.sql.*;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "youxianqi.yixi.model.MainResourceUserTagR")
@Table(name = "main_resource_user_tag_r")
public class MainResourceUserTagR {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"id\"", nullable = false)
  private Integer id;
  @Column(name = "\"resource_id\"", nullable = false)
  private Integer resourceId;
  @Column(name = "\"user_id\"", nullable = true)
  private Integer userId;
  @Column(name = "\"tag_id\"", nullable = false)
  private Integer tagId;
  @Column(name = "\"local_update_time\"", nullable = false)
  private Timestamp localUpdateTime;
}