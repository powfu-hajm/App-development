package org.example.emotionbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * MBTI 人格类型实体类
 */
@Data
@TableName("mbti_type")
public class MbtiType implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * MBTI类型代码，如ENTP
     */
    @TableField("type_code")
    private String typeCode;

    /**
     * 类型中文名称，如辩论家
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 类型英文名称，如The Debater
     */
    @TableField("type_name_en")
    private String typeNameEn;

    /**
     * 类型分组：分析家/外交家/守护者/探险家
     */
    @TableField("type_group")
    private String typeGroup;

    /**
     * 简短描述
     */
    @TableField("brief_desc")
    private String briefDesc;

    /**
     * 详细描述
     */
    @TableField("detail_desc")
    private String detailDesc;

    /**
     * 优势特点，JSON数组格式
     */
    @TableField("strengths")
    private String strengths;

    /**
     * 潜在弱点，JSON数组格式
     */
    @TableField("weaknesses")
    private String weaknesses;

    /**
     * 职业建议，JSON数组格式
     */
    @TableField("career_suggestions")
    private String careerSuggestions;

    /**
     * 著名人物，JSON数组格式
     */
    @TableField("famous_people")
    private String famousPeople;

    /**
     * 图片文件名
     */
    @TableField("image_name")
    private String imageName;

    /**
     * 主题色
     */
    @TableField("color_primary")
    private String colorPrimary;

    /**
     * 辅助色
     */
    @TableField("color_secondary")
    private String colorSecondary;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
