/*
 心理测试功能数据库表
 Date: 2024-12-01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 问卷表 (test_paper)
-- ----------------------------
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

-- ----------------------------
-- 2. 题目表 (test_question)
-- ----------------------------
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

-- ----------------------------
-- 3. 选项表 (test_option)
-- ----------------------------
DROP TABLE IF EXISTS `test_option`;
CREATE TABLE `test_option` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `question_id` BIGINT NOT NULL COMMENT '所属题目ID',
    `option_order` INT NOT NULL COMMENT '选项顺序(A=1, B=2...)',
    `content` VARCHAR(500) NOT NULL COMMENT '选项内容',
    `score` INT NOT NULL DEFAULT 0 COMMENT '该选项得分',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目选项表';

-- ----------------------------
-- 4. 结果区间表 (test_result_range)
-- ----------------------------
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

-- ----------------------------
-- 5. 用户测试记录表 (test_record)
-- ----------------------------
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

-- ========================================
-- 插入测试数据：SDS抑郁自评量表
-- ========================================

-- 插入问卷
INSERT INTO `test_paper` (`id`, `title`, `description`, `question_count`, `estimated_time`, `icon`) VALUES
(1, 'SDS抑郁自评量表', 'SDS（Self-Rating Depression Scale）是由Zung于1965年编制的，用于评定抑郁症状的轻重程度及其在治疗中的变化。请根据您最近一周的实际感觉选择最符合的选项。', 20, 5, 'depression');

-- 插入20道题目
INSERT INTO `test_question` (`id`, `paper_id`, `question_order`, `content`) VALUES
(1, 1, 1, '我感到情绪沮丧，郁闷'),
(2, 1, 2, '我感到早晨心情最好'),
(3, 1, 3, '我要哭或想哭'),
(4, 1, 4, '我夜间睡眠不好'),
(5, 1, 5, '我吃饭像平时一样多'),
(6, 1, 6, '我的性功能正常'),
(7, 1, 7, '我感到体重减轻'),
(8, 1, 8, '我为便秘烦恼'),
(9, 1, 9, '我的心跳比平时快'),
(10, 1, 10, '我无故感到疲劳'),
(11, 1, 11, '我的头脑像往常一样清楚'),
(12, 1, 12, '我做事情像平时一样不感到困难'),
(13, 1, 13, '我坐卧不安，难以保持平静'),
(14, 1, 14, '我对未来感到有希望'),
(15, 1, 15, '我比平时更容易激怒'),
(16, 1, 16, '我觉得决定什么事很容易'),
(17, 1, 17, '我感到自己是有用的和不可缺少的人'),
(18, 1, 18, '我的生活很有意义'),
(19, 1, 19, '假若我死了别人会过得更好'),
(20, 1, 20, '我仍旧喜欢自己平时喜欢的东西');

-- 插入选项（每题4个选项）
-- 正向计分题目：1,3,4,7,8,9,10,13,15,19 (选项A=1分,B=2分,C=3分,D=4分)
-- 反向计分题目：2,5,6,11,12,14,16,17,18,20 (选项A=4分,B=3分,C=2分,D=1分)

-- 题目1（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(1, 1, '没有或很少有', 1),
(1, 2, '有时有', 2),
(1, 3, '大部分时间有', 3),
(1, 4, '绝大部分或全部时间都有', 4);

-- 题目2（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(2, 1, '没有或很少有', 4),
(2, 2, '有时有', 3),
(2, 3, '大部分时间有', 2),
(2, 4, '绝大部分或全部时间都有', 1);

-- 题目3（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(3, 1, '没有或很少有', 1),
(3, 2, '有时有', 2),
(3, 3, '大部分时间有', 3),
(3, 4, '绝大部分或全部时间都有', 4);

-- 题目4（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(4, 1, '没有或很少有', 1),
(4, 2, '有时有', 2),
(4, 3, '大部分时间有', 3),
(4, 4, '绝大部分或全部时间都有', 4);

-- 题目5（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(5, 1, '没有或很少有', 4),
(5, 2, '有时有', 3),
(5, 3, '大部分时间有', 2),
(5, 4, '绝大部分或全部时间都有', 1);

-- 题目6（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(6, 1, '没有或很少有', 4),
(6, 2, '有时有', 3),
(6, 3, '大部分时间有', 2),
(6, 4, '绝大部分或全部时间都有', 1);

-- 题目7（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(7, 1, '没有或很少有', 1),
(7, 2, '有时有', 2),
(7, 3, '大部分时间有', 3),
(7, 4, '绝大部分或全部时间都有', 4);

-- 题目8（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(8, 1, '没有或很少有', 1),
(8, 2, '有时有', 2),
(8, 3, '大部分时间有', 3),
(8, 4, '绝大部分或全部时间都有', 4);

-- 题目9（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(9, 1, '没有或很少有', 1),
(9, 2, '有时有', 2),
(9, 3, '大部分时间有', 3),
(9, 4, '绝大部分或全部时间都有', 4);

-- 题目10（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(10, 1, '没有或很少有', 1),
(10, 2, '有时有', 2),
(10, 3, '大部分时间有', 3),
(10, 4, '绝大部分或全部时间都有', 4);

-- 题目11（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(11, 1, '没有或很少有', 4),
(11, 2, '有时有', 3),
(11, 3, '大部分时间有', 2),
(11, 4, '绝大部分或全部时间都有', 1);

-- 题目12（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(12, 1, '没有或很少有', 4),
(12, 2, '有时有', 3),
(12, 3, '大部分时间有', 2),
(12, 4, '绝大部分或全部时间都有', 1);

-- 题目13（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(13, 1, '没有或很少有', 1),
(13, 2, '有时有', 2),
(13, 3, '大部分时间有', 3),
(13, 4, '绝大部分或全部时间都有', 4);

-- 题目14（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(14, 1, '没有或很少有', 4),
(14, 2, '有时有', 3),
(14, 3, '大部分时间有', 2),
(14, 4, '绝大部分或全部时间都有', 1);

-- 题目15（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(15, 1, '没有或很少有', 1),
(15, 2, '有时有', 2),
(15, 3, '大部分时间有', 3),
(15, 4, '绝大部分或全部时间都有', 4);

-- 题目16（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(16, 1, '没有或很少有', 4),
(16, 2, '有时有', 3),
(16, 3, '大部分时间有', 2),
(16, 4, '绝大部分或全部时间都有', 1);

-- 题目17（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(17, 1, '没有或很少有', 4),
(17, 2, '有时有', 3),
(17, 3, '大部分时间有', 2),
(17, 4, '绝大部分或全部时间都有', 1);

-- 题目18（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(18, 1, '没有或很少有', 4),
(18, 2, '有时有', 3),
(18, 3, '大部分时间有', 2),
(18, 4, '绝大部分或全部时间都有', 1);

-- 题目19（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(19, 1, '没有或很少有', 1),
(19, 2, '有时有', 2),
(19, 3, '大部分时间有', 3),
(19, 4, '绝大部分或全部时间都有', 4);

-- 题目20（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(20, 1, '没有或很少有', 4),
(20, 2, '有时有', 3),
(20, 3, '大部分时间有', 2),
(20, 4, '绝大部分或全部时间都有', 1);

-- 插入结果区间（SDS标准：粗分20-80，标准分=粗分*1.25）
-- 这里直接用粗分计算
INSERT INTO `test_result_range` (`paper_id`, `min_score`, `max_score`, `result_level`, `result_title`, `result_description`, `suggestion`) VALUES
(1, 20, 39, 'normal', '心理状态良好', '您的测试结果显示心理状态良好，没有明显的抑郁症状。您能够较好地应对生活中的压力和挑战，保持积极乐观的心态。', '继续保持良好的生活习惯，适当运动，与亲友保持联系，培养兴趣爱好。'),
(1, 40, 47, 'mild', '轻度抑郁倾向', '您的测试结果显示存在轻度抑郁倾向。您可能在某些时候感到情绪低落、疲劳或兴趣减退，但这些症状尚未严重影响您的日常生活。', '建议适当放松心情，增加户外活动，与信任的朋友或家人交流。如果症状持续，可以考虑寻求专业心理咨询。'),
(1, 48, 55, 'moderate', '中度抑郁症状', '您的测试结果显示存在中度抑郁症状。您可能经常感到情绪低落、睡眠困难、食欲改变或注意力难以集中，这些症状可能已经影响到您的工作和生活。', '强烈建议您寻求专业心理咨询或就医。同时，请保持规律作息，适当运动，避免独处太久，必要时可以向亲友倾诉。'),
(1, 56, 80, 'severe', '重度抑郁症状', '您的测试结果显示存在重度抑郁症状。您可能长期处于情绪极度低落的状态，对生活失去兴趣，甚至可能有消极的想法。这需要引起高度重视。', '请尽快寻求专业精神科医生的帮助。如果您有任何自我伤害的想法，请立即拨打心理援助热线或前往医院就诊。您并不孤单，专业的帮助可以让您走出困境。');

SET FOREIGN_KEY_CHECKS = 1;
