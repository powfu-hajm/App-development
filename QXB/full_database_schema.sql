-- =====================================================
-- QXB (青心伴) 完整数据库架构脚本
-- 用于新设备迁移
-- 生成时间: 2024-12
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库 (如果不存在)
CREATE DATABASE IF NOT EXISTS `mood_diary`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `mood_diary`;

-- =====================================================
-- 1. 用户表
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(MD5加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始管理员账户 (密码: root)
INSERT INTO `user` (`username`, `password`, `nickname`) VALUES
('root', '63a9f0ea7bb98050796b649e85481845', '超级管理员');

-- =====================================================
-- 2. 日记表
-- =====================================================
DROP TABLE IF EXISTS `diary`;
CREATE TABLE `diary` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` TEXT COMMENT '日记内容',
    `mood_tag` VARCHAR(50) COMMENT '情绪标签',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '软删除标志: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日记表';

-- =====================================================
-- 3. AI聊天记录表
-- =====================================================
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(10) NOT NULL COMMENT '角色: user 或 assistant',
    `content` TEXT NOT NULL COMMENT '聊天内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天记录表';

-- =====================================================
-- 4. 心理文章表
-- =====================================================
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
    `summary` TEXT COMMENT '文章简介/摘要',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `original_url` VARCHAR(500) NOT NULL COMMENT '原文链接',
    `source` VARCHAR(50) DEFAULT '简单心理' COMMENT '来源/站点',
    `type` VARCHAR(50) DEFAULT NULL COMMENT '文章类型/分类，如 专栏/测试/科普',
    `read_count` INT DEFAULT 0 COMMENT '阅读量(虚拟)',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间(爬取自原文)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '抓取入库时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_original_url` (`original_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心理文章表';

-- =====================================================
-- 5. 心理测试 - 问卷表
-- =====================================================
DROP TABLE IF EXISTS `test_paper`;
CREATE TABLE `test_paper` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL COMMENT '问卷标题',
    `description` TEXT COMMENT '问卷描述',
    `question_count` INT NOT NULL DEFAULT 0 COMMENT '题目数量',
    `estimated_time` INT NOT NULL DEFAULT 5 COMMENT '预计用时(分钟)',
    `icon` VARCHAR(50) DEFAULT 'default' COMMENT '图标标识',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心理测试问卷表';

-- =====================================================
-- 6. 心理测试 - 题目表
-- =====================================================
DROP TABLE IF EXISTS `test_question`;
CREATE TABLE `test_question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `paper_id` BIGINT NOT NULL COMMENT '所属问卷ID',
    `question_order` INT NOT NULL COMMENT '题目顺序(从1开始)',
    `content` TEXT NOT NULL COMMENT '题目内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_paper_id` (`paper_id`),
    INDEX `idx_order` (`paper_id`, `question_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试题目表';

-- =====================================================
-- 7. 心理测试 - 选项表 (含MBTI维度)
-- =====================================================
DROP TABLE IF EXISTS `test_option`;
CREATE TABLE `test_option` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `question_id` BIGINT NOT NULL COMMENT '所属题目ID',
    `option_order` INT NOT NULL COMMENT '选项顺序(A=1, B=2...)',
    `content` VARCHAR(500) NOT NULL COMMENT '选项内容',
    `score` INT NOT NULL DEFAULT 0 COMMENT '该选项得分',
    `dimension` VARCHAR(10) DEFAULT NULL COMMENT 'MBTI维度标识：E/I/S/N/T/F/J/P',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';

-- =====================================================
-- 8. 心理测试 - 结果区间表
-- =====================================================
DROP TABLE IF EXISTS `test_result_range`;
CREATE TABLE `test_result_range` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `paper_id` BIGINT NOT NULL COMMENT '所属问卷ID',
    `min_score` INT NOT NULL COMMENT '最低分(含)',
    `max_score` INT NOT NULL COMMENT '最高分(含)',
    `result_level` VARCHAR(50) NOT NULL COMMENT '结果等级',
    `result_title` VARCHAR(100) NOT NULL COMMENT '结果标题',
    `result_description` TEXT NOT NULL COMMENT '结果描述',
    `suggestion` TEXT COMMENT '建议',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_paper_id` (`paper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试结果区间表';

-- =====================================================
-- 9. 心理测试 - 用户测试记录表
-- =====================================================
DROP TABLE IF EXISTS `test_record`;
CREATE TABLE `test_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `paper_id` BIGINT NOT NULL COMMENT '问卷ID',
    `total_score` INT NOT NULL COMMENT '总得分',
    `result_level` VARCHAR(50) NOT NULL COMMENT '结果等级',
    `result_title` VARCHAR(100) NOT NULL COMMENT '结果标题',
    `result_description` TEXT NOT NULL COMMENT '结果描述',
    `suggestion` TEXT COMMENT '建议',
    `answers` TEXT COMMENT '用户答案JSON',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_paper_id` (`paper_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户测试记录表';

-- =====================================================
-- 10. MBTI人格类型表
-- =====================================================
DROP TABLE IF EXISTS `mbti_type`;
CREATE TABLE `mbti_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `type_code` VARCHAR(4) NOT NULL COMMENT 'MBTI类型代码，如ENTP',
    `type_name` VARCHAR(50) NOT NULL COMMENT '类型中文名称，如辩论家',
    `type_name_en` VARCHAR(50) NOT NULL COMMENT '类型英文名称，如The Debater',
    `type_group` VARCHAR(20) NOT NULL COMMENT '类型分组：分析家/外交家/守护者/探险家',
    `brief_desc` VARCHAR(200) NOT NULL COMMENT '简短描述',
    `detail_desc` TEXT NOT NULL COMMENT '详细描述',
    `strengths` TEXT NOT NULL COMMENT '优势特点，JSON数组格式',
    `weaknesses` TEXT NOT NULL COMMENT '潜在弱点，JSON数组格式',
    `career_suggestions` TEXT COMMENT '职业建议，JSON数组格式',
    `famous_people` TEXT COMMENT '著名人物，JSON数组格式',
    `image_name` VARCHAR(100) COMMENT '图片文件名',
    `color_primary` VARCHAR(20) DEFAULT '#7C4DFF' COMMENT '主题色',
    `color_secondary` VARCHAR(20) DEFAULT '#B388FF' COMMENT '辅助色',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MBTI人格类型表';

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 说明:
-- 1. 此脚本创建所有表结构，不包含测试数据
-- 2. 测试数据请单独运行: test_tables.sql
-- 3. MBTI类型数据请运行: mbti_types.sql (仅INSERT部分)
-- =====================================================
