package org.example.emotionbackend.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        log.info("=== MyMetaObjectHandler 插入填充开始 ===");

        // 使用更可靠的方式设置字段
        if (metaObject.hasSetter("createTime")) {
            Object currentValue = this.getFieldValByName("createTime", metaObject);
            if (currentValue == null) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                log.info("插入填充 - 设置 createTime: {}", now);
            }
        }

        if (metaObject.hasSetter("updateTime")) {
            Object currentValue = this.getFieldValByName("updateTime", metaObject);
            if (currentValue == null) {
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
                log.info("插入填充 - 设置 updateTime: {}", now);
            }
        }

        log.info("=== MyMetaObjectHandler 插入填充完成 ===");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        log.info("=== MyMetaObjectHandler 更新填充开始 ===");

        // 强制设置updateTime - 使用更可靠的方法
        try {
            this.setFieldValByName("updateTime", now, metaObject);
            log.info("更新填充 - 强制设置 updateTime: {}", now);

            // 额外检查：确保字段确实被设置
            Object updateTimeValue = this.getFieldValByName("updateTime", metaObject);
            if (updateTimeValue != null) {
                log.info("更新填充 - 确认 updateTime 已设置: {}", updateTimeValue);
            } else {
                log.error("更新填充 - updateTime 设置失败，值为null");
            }
        } catch (Exception e) {
            log.error("更新填充时设置 updateTime 失败", e);
        }

        log.info("=== MyMetaObjectHandler 更新填充完成 ===");
    }
}