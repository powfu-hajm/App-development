-- 给 diary 表添加逻辑删除字段 deleted，默认值为 0 (未删除)
ALTER TABLE `diary` ADD COLUMN `deleted` int(11) DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除';

