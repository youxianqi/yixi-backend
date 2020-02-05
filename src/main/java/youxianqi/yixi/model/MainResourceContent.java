package youxianqi.yixi.model;

import java.sql.*;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity(name = "youxianqi.yixi.model.MainResourceContent")
@Table(name = "main_resource_content")
public class MainResourceContent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"id\"", nullable = false)
  private Integer id;
  @Column(name = "\"resource_id\"", nullable = false)
  private Integer resourceId;
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
  @Column(name = "\"content_seq\"", nullable = false)
  private Byte contentSeq;
  @Column(name = "\"media_url\"", nullable = true)
  private String mediaUrl;
  @Column(name = "\"text_content\"", nullable = true)
  private String textContent;
  @Column(name = "\"ei_question\"", nullable = true)
  private String eiQuestion;
  @Column(name = "\"ei_explain\"", nullable = true)
  private String eiExplain;
  @Column(name = "\"ei_correct_ans_cnt\"", nullable = true)
  private Byte eiCorrectAnsCnt;
  @Column(name = "\"ei_correct_ans_list_json\"", nullable = true)
  private String eiCorrectAnsListJson;
}