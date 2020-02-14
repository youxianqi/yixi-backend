package youxianqi.yixi.model;

import java.sql.*;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity(name = "youxianqi.yixi.model.MainApplyPublic")
@Table(name = "main_apply_public")
public class MainApplyPublic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"id\"", nullable = false)
  private Integer id;
  @Column(name = "\"resource_id\"", nullable = false)
  private Integer resourceId;
  @Column(name = "\"user_id\"", nullable = false)
  private Integer userId;
  @Column(name = "\"status\"", nullable = false)
  private Byte status = (byte)0;
  @Column(name = "\"local_update_time\"", nullable = false)
  @UpdateTimestamp
  private Timestamp localUpdateTime;
}