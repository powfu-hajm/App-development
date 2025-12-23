package org.example.emotionbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String summary;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("original_url")
    private String originalUrl;

    private String source;

    // 确保有 @TableField 注解
    @TableField(value = "type")
    private String type;

    @TableField("read_count")
    private Integer readCount;

    @TableField("publish_time")
    private LocalDateTime publishTime;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 获取类型ID（将字符串类型转换为整数ID）
     */
    public Integer getTypeId() {
        if (type == null) return null;

        switch (type) {
            case "专栏": return 1;
            case "测试": return 2;
            case "科普": return 3;
            case "自我成长": return 4;
            case "情感关系": return 5;
            default: return null;
        }
    }

    /**
     * 设置类型ID（将整数ID转换为字符串类型）
     */
    public void setTypeId(Integer typeId) {
        if (typeId == null) {
            this.type = null;
            return;
        }

        switch (typeId) {
            case 1: this.type = "专栏"; break;
            case 2: this.type = "测试"; break;
            case 3: this.type = "科普"; break;
            case 4: this.type = "自我成长"; break;
            case 5: this.type = "情感关系"; break;
            default: this.type = null;
        }
    }
}