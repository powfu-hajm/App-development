package org.example.emotionbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("test_record")
public class TestRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("paper_id")
    private Long paperId;

    @TableField("total_score")
    private Integer totalScore;

    @TableField("result_level")
    private String resultLevel;

    @TableField("result_title")
    private String resultTitle;

    @TableField("result_description")
    private String resultDescription;

    @TableField("suggestion")
    private String suggestion;

    @TableField("answers")
    private String answers;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
