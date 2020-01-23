package youxianqi.yixi.model;

import java.sql.*;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity(name = "youxianqi.yixi.model.MainTagDict")
@Table(name = "main_tag_dict")
public class MainTagDict {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"tag_id\"", nullable = false)
  private Integer tagId;
  @Column(name = "\"tag_name\"", nullable = false)
  private String tagName;
  /**
   * ResourceType,1,course,课程
   * ResourceType,2,exercise,习题
   * ResourceType,3,presentation,成果展示
   * ResourceType,4,entertainment,娱乐
   * ResourceType,5,social,社交
   */
  @Column(name = "\"tag_resource_type\"", nullable = false)
  private Byte tagResourceType;
  @Column(name = "\"local_update_time\"", nullable = false)
  @UpdateTimestamp
  private Timestamp localUpdateTime;
}