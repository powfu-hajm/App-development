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

-- ========================================
-- 插入测试数据：SAS焦虑自评量表 (paper_id=2)
-- ========================================

INSERT INTO `test_paper` (`id`, `title`, `description`, `question_count`, `estimated_time`, `icon`) VALUES
(2, 'SAS焦虑自评量表', 'SAS（Self-Rating Anxiety Scale）是由Zung于1971年编制的，用于评定焦虑症状的轻重程度。请根据您最近一周的实际感觉选择最符合的选项。', 20, 5, 'anxiety');

-- SAS 20道题目
INSERT INTO `test_question` (`id`, `paper_id`, `question_order`, `content`) VALUES
(21, 2, 1, '我感到比平时更紧张和焦虑'),
(22, 2, 2, '我无缘无故地感到害怕'),
(23, 2, 3, '我容易心烦意乱或感到惊恐'),
(24, 2, 4, '我感到我可能要发疯'),
(25, 2, 5, '我感到一切都很好，不会有什么不幸发生'),
(26, 2, 6, '我的手脚发抖'),
(27, 2, 7, '我因头痛、颈痛和背痛而苦恼'),
(28, 2, 8, '我感到身体虚弱，容易疲劳'),
(29, 2, 9, '我能够安静地坐着，并且容易保持平静'),
(30, 2, 10, '我感到心跳加快'),
(31, 2, 11, '我因阵阵头晕而苦恼'),
(32, 2, 12, '我有过晕倒或觉得要晕倒的感觉'),
(33, 2, 13, '我呼吸时进气和出气都很容易'),
(34, 2, 14, '我的手脚感到麻木和刺痛'),
(35, 2, 15, '我因胃痛和消化不良而苦恼'),
(36, 2, 16, '我经常需要排尿'),
(37, 2, 17, '我的手常常是干燥温暖的'),
(38, 2, 18, '我脸红发热'),
(39, 2, 19, '我容易入睡并且一夜睡得很好'),
(40, 2, 20, '我做噩梦');

-- SAS选项（正向：1,2,3,4,6,7,8,10,11,12,14,15,16,18,20  反向：5,9,13,17,19）
-- 题目21-24（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(21, 1, '没有或很少有', 1), (21, 2, '有时有', 2), (21, 3, '大部分时间有', 3), (21, 4, '绝大部分或全部时间都有', 4),
(22, 1, '没有或很少有', 1), (22, 2, '有时有', 2), (22, 3, '大部分时间有', 3), (22, 4, '绝大部分或全部时间都有', 4),
(23, 1, '没有或很少有', 1), (23, 2, '有时有', 2), (23, 3, '大部分时间有', 3), (23, 4, '绝大部分或全部时间都有', 4),
(24, 1, '没有或很少有', 1), (24, 2, '有时有', 2), (24, 3, '大部分时间有', 3), (24, 4, '绝大部分或全部时间都有', 4);

-- 题目25（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(25, 1, '没有或很少有', 4), (25, 2, '有时有', 3), (25, 3, '大部分时间有', 2), (25, 4, '绝大部分或全部时间都有', 1);

-- 题目26-28（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(26, 1, '没有或很少有', 1), (26, 2, '有时有', 2), (26, 3, '大部分时间有', 3), (26, 4, '绝大部分或全部时间都有', 4),
(27, 1, '没有或很少有', 1), (27, 2, '有时有', 2), (27, 3, '大部分时间有', 3), (27, 4, '绝大部分或全部时间都有', 4),
(28, 1, '没有或很少有', 1), (28, 2, '有时有', 2), (28, 3, '大部分时间有', 3), (28, 4, '绝大部分或全部时间都有', 4);

-- 题目29（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(29, 1, '没有或很少有', 4), (29, 2, '有时有', 3), (29, 3, '大部分时间有', 2), (29, 4, '绝大部分或全部时间都有', 1);

-- 题目30-32（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(30, 1, '没有或很少有', 1), (30, 2, '有时有', 2), (30, 3, '大部分时间有', 3), (30, 4, '绝大部分或全部时间都有', 4),
(31, 1, '没有或很少有', 1), (31, 2, '有时有', 2), (31, 3, '大部分时间有', 3), (31, 4, '绝大部分或全部时间都有', 4),
(32, 1, '没有或很少有', 1), (32, 2, '有时有', 2), (32, 3, '大部分时间有', 3), (32, 4, '绝大部分或全部时间都有', 4);

-- 题目33（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(33, 1, '没有或很少有', 4), (33, 2, '有时有', 3), (33, 3, '大部分时间有', 2), (33, 4, '绝大部分或全部时间都有', 1);

-- 题目34-36（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(34, 1, '没有或很少有', 1), (34, 2, '有时有', 2), (34, 3, '大部分时间有', 3), (34, 4, '绝大部分或全部时间都有', 4),
(35, 1, '没有或很少有', 1), (35, 2, '有时有', 2), (35, 3, '大部分时间有', 3), (35, 4, '绝大部分或全部时间都有', 4),
(36, 1, '没有或很少有', 1), (36, 2, '有时有', 2), (36, 3, '大部分时间有', 3), (36, 4, '绝大部分或全部时间都有', 4);

-- 题目37（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(37, 1, '没有或很少有', 4), (37, 2, '有时有', 3), (37, 3, '大部分时间有', 2), (37, 4, '绝大部分或全部时间都有', 1);

-- 题目38（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(38, 1, '没有或很少有', 1), (38, 2, '有时有', 2), (38, 3, '大部分时间有', 3), (38, 4, '绝大部分或全部时间都有', 4);

-- 题目39（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(39, 1, '没有或很少有', 4), (39, 2, '有时有', 3), (39, 3, '大部分时间有', 2), (39, 4, '绝大部分或全部时间都有', 1);

-- 题目40（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(40, 1, '没有或很少有', 1), (40, 2, '有时有', 2), (40, 3, '大部分时间有', 3), (40, 4, '绝大部分或全部时间都有', 4);

-- SAS结果区间
INSERT INTO `test_result_range` (`paper_id`, `min_score`, `max_score`, `result_level`, `result_title`, `result_description`, `suggestion`) VALUES
(2, 20, 39, 'normal', '心理状态良好', '您的测试结果显示没有明显的焦虑症状，心理状态良好。您能够较好地应对生活中的压力和不确定性。', '继续保持良好的心态，适当运动和放松，保持规律的作息时间。'),
(2, 40, 47, 'mild', '轻度焦虑倾向', '您的测试结果显示存在轻度焦虑倾向。您可能在某些情况下感到紧张、担忧或不安，但这些症状尚未严重影响您的日常生活。', '建议学习一些放松技巧如深呼吸、冥想等，适当减少咖啡因摄入，保持规律运动。如症状持续可考虑心理咨询。'),
(2, 48, 55, 'moderate', '中度焦虑症状', '您的测试结果显示存在中度焦虑症状。您可能经常感到紧张不安、担忧、心悸或睡眠困难，这些症状可能已经影响到您的工作和生活质量。', '建议您寻求专业心理咨询。同时可以尝试放松训练、适度运动、保持社交。必要时配合医生进行药物治疗。'),
(2, 56, 80, 'severe', '重度焦虑症状', '您的测试结果显示存在重度焦虑症状。您可能长期处于紧张、恐惧的状态，伴有明显的躯体症状，严重影响日常生活和工作。', '请尽快寻求专业精神科医生的帮助。焦虑症是可以治疗的，通过专业的心理治疗和必要的药物治疗，大多数患者都能得到显著改善。');

-- ========================================
-- 插入测试数据：PSS压力知觉量表 (paper_id=3)
-- ========================================

INSERT INTO `test_paper` (`id`, `title`, `description`, `question_count`, `estimated_time`, `icon`) VALUES
(3, 'PSS压力知觉量表', 'PSS（Perceived Stress Scale）是由Cohen于1Mo983年编制的，用于测量个体在最近一个月内感受到的压力程度。请根据您最近一个月的实际感觉选择最符合的选项。', 14, 4, 'stress');

-- PSS 14道题目
INSERT INTO `test_question` (`id`, `paper_id`, `question_order`, `content`) VALUES
(41, 3, 1, '因为发生了意想不到的事情而感到心烦'),
(42, 3, 2, '感到无法控制生活中重要的事情'),
(43, 3, 3, '感到紧张和有压力'),
(44, 3, 4, '成功地处理了生活中的烦心事'),
(45, 3, 5, '有效地应对了生活中的重大变化'),
(46, 3, 6, '对自己处理个人问题的能力充满信心'),
(47, 3, 7, '感到事情进展顺利'),
(48, 3, 8, '发现无法应付所有必须要做的事情'),
(49, 3, 9, '能够控制生活中的烦躁情绪'),
(50, 3, 10, '觉得自己掌控了一切'),
(51, 3, 11, '因为事情超出了自己的控制范围而感到愤怒'),
(52, 3, 12, '发现自己在想那些必须要完成的事情'),
(53, 3, 13, '能够把握时间的使用'),
(54, 3, 14, '感到困难堆积如山，无法克服');

-- PSS选项（正向：1,2,3,8,11,12,14  反向：4,5,6,7,9,10,13）
-- 评分：0-4分制（从不、偶尔、有时、经常、总是）

-- 题目41-43（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(41, 1, '从不', 0), (41, 2, '偶尔', 1), (41, 3, '有时', 2), (41, 4, '经常', 3), (41, 5, '总是', 4),
(42, 1, '从不', 0), (42, 2, '偶尔', 1), (42, 3, '有时', 2), (42, 4, '经常', 3), (42, 5, '总是', 4),
(43, 1, '从不', 0), (43, 2, '偶尔', 1), (43, 3, '有时', 2), (43, 4, '经常', 3), (43, 5, '总是', 4);

-- 题目44-47（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(44, 1, '从不', 4), (44, 2, '偶尔', 3), (44, 3, '有时', 2), (44, 4, '经常', 1), (44, 5, '总是', 0),
(45, 1, '从不', 4), (45, 2, '偶尔', 3), (45, 3, '有时', 2), (45, 4, '经常', 1), (45, 5, '总是', 0),
(46, 1, '从不', 4), (46, 2, '偶尔', 3), (46, 3, '有时', 2), (46, 4, '经常', 1), (46, 5, '总是', 0),
(47, 1, '从不', 4), (47, 2, '偶尔', 3), (47, 3, '有时', 2), (47, 4, '经常', 1), (47, 5, '总是', 0);

-- 题目48（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(48, 1, '从不', 0), (48, 2, '偶尔', 1), (48, 3, '有时', 2), (48, 4, '经常', 3), (48, 5, '总是', 4);

-- 题目49-50（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(49, 1, '从不', 4), (49, 2, '偶尔', 3), (49, 3, '有时', 2), (49, 4, '经常', 1), (49, 5, '总是', 0),
(50, 1, '从不', 4), (50, 2, '偶尔', 3), (50, 3, '有时', 2), (50, 4, '经常', 1), (50, 5, '总是', 0);

-- 题目51-52（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(51, 1, '从不', 0), (51, 2, '偶尔', 1), (51, 3, '有时', 2), (51, 4, '经常', 3), (51, 5, '总是', 4),
(52, 1, '从不', 0), (52, 2, '偶尔', 1), (52, 3, '有时', 2), (52, 4, '经常', 3), (52, 5, '总是', 4);

-- 题目53（反向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(53, 1, '从不', 4), (53, 2, '偶尔', 3), (53, 3, '有时', 2), (53, 4, '经常', 1), (53, 5, '总是', 0);

-- 题目54（正向）
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(54, 1, '从不', 0), (54, 2, '偶尔', 1), (54, 3, '有时', 2), (54, 4, '经常', 3), (54, 5, '总是', 4);

-- PSS结果区间（总分0-56）
INSERT INTO `test_result_range` (`paper_id`, `min_score`, `max_score`, `result_level`, `result_title`, `result_description`, `suggestion`) VALUES
(3, 0, 13, 'normal', '压力水平较低', '您的压力知觉水平较低，说明您在过去一个月内感受到的压力相对较小，能够较好地应对日常生活中的各种挑战。', '继续保持良好的生活方式，适当运动，保持社交活动，培养积极的应对方式。'),
(3, 14, 26, 'mild', '压力水平中等', '您的压力知觉处于中等水平，这是大多数人的正常范围。您可能在某些时候感到有压力，但总体上能够应对。', '建议定期进行放松活动，如运动、冥想、与朋友交流等。学习时间管理技巧，适当说"不"，避免过度承担任务。'),
(3, 27, 40, 'moderate', '压力水平较高', '您的压力知觉水平较高，说明您在过去一个月内感受到了较大的压力。长期的高压力可能会影响身心健康。', '建议积极采取措施减压：规律作息、适度运动、学习放松技巧、寻求社会支持。如果感到难以应对，建议咨询心理专业人士。'),
(3, 41, 56, 'severe', '压力水平很高', '您的压力知觉水平很高，说明您正在承受很大的压力。这种状态如果持续可能会对身心健康造成严重影响。', '强烈建议您寻求专业帮助。同时请注意：保证充足睡眠、减少不必要的任务、寻求他人帮助、进行放松活动。您的健康最重要，请不要独自承担所有压力。');

-- ========================================
-- 插入测试数据：MBTI简易人格测试 (paper_id=4)
-- ========================================

INSERT INTO `test_paper` (`id`, `title`, `description`, `question_count`, `estimated_time`, `icon`) VALUES
(4, 'MBTI简易人格测试', 'MBTI（Myers-Briggs Type Indicator）是一种广泛使用的人格类型指标。本测试为简易版本，包含28道题目，帮助您初步了解自己的人格倾向。请选择最符合您日常行为习惯的选项。', 28, 8, 'mbti');

-- MBTI 28道题目（每个维度7题）
-- E/I 外向/内向 (题目55-61)
-- S/N 感觉/直觉 (题目62-68)
-- T/F 思考/情感 (题目69-75)
-- J/P 判断/知觉 (题目76-82)

INSERT INTO `test_question` (`id`, `paper_id`, `question_order`, `content`) VALUES
-- E/I 维度
(55, 4, 1, '在社交场合中，我通常'),
(56, 4, 2, '周末时，我更喜欢'),
(57, 4, 3, '在工作或学习中，我更喜欢'),
(58, 4, 4, '当我需要做重要决定时'),
(59, 4, 5, '在聚会上，我通常'),
(60, 4, 6, '我从哪里获得能量'),
(61, 4, 7, '与人交流时，我更倾向于'),
-- S/N 维度
(62, 4, 8, '我更关注'),
(63, 4, 9, '在学习新事物时，我喜欢'),
(64, 4, 10, '我更相信'),
(65, 4, 11, '我描述事物时倾向于'),
(66, 4, 12, '在解决问题时，我更喜欢'),
(67, 4, 13, '我对未来的态度是'),
(68, 4, 14, '我更喜欢的工作方式是'),
-- T/F 维度
(69, 4, 15, '做决定时，我更看重'),
(70, 4, 16, '当朋友遇到困难时，我通常'),
(71, 4, 17, '在评价他人时，我更注重'),
(72, 4, 18, '在争论中，我更关心'),
(73, 4, 19, '我更欣赏的品质是'),
(74, 4, 20, '给他人反馈时，我倾向于'),
(75, 4, 21, '我认为更重要的是'),
-- J/P 维度
(76, 4, 22, '我的生活方式更倾向于'),
(77, 4, 23, '面对截止日期，我通常'),
(78, 4, 24, '我的工作空间通常'),
(79, 4, 25, '制定计划时，我更喜欢'),
(80, 4, 26, '在旅行时，我更喜欢'),
(81, 4, 27, '我对变化的态度是'),
(82, 4, 28, '完成任务时，我更倾向于');

-- MBTI选项
-- E=1分, I=2分 (题目55-61)
-- S=1分, N=2分 (题目62-68)
-- T=1分, F=2分 (题目69-75)
-- J=1分, P=2分 (题目76-82)

-- E/I 维度选项
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(55, 1, '主动与他人交谈，认识新朋友', 1), (55, 2, '等待他人来找我交谈，或与熟人待在一起', 2),
(56, 1, '参加社交活动或与朋友聚会', 1), (56, 2, '独自待在家里或进行安静的活动', 2),
(57, 1, '与他人合作，进行讨论', 1), (57, 2, '独立工作，有自己的空间', 2),
(58, 1, '与他人讨论，听取多方意见', 1), (58, 2, '自己思考，做出判断', 2),
(59, 1, '是活跃的参与者，喜欢成为焦点', 1), (59, 2, '是安静的观察者，喜欢在边缘', 2),
(60, 1, '与人互动让我充满活力', 1), (60, 2, '独处让我恢复精力', 2),
(61, 1, '边想边说，通过交谈理清思路', 1), (61, 2, '先想好再说，喜欢深思熟虑', 2);

-- S/N 维度选项
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(62, 1, '具体的事实和细节', 1), (62, 2, '整体的模式和可能性', 2),
(63, 1, '循序渐进，从基础开始', 1), (63, 2, '先了解整体概念，再学细节', 2),
(64, 1, '实际经验和已证实的方法', 1), (64, 2, '直觉和灵感', 2),
(65, 1, '具体、准确、注重细节', 1), (65, 2, '抽象、概括、注重联系', 2),
(66, 1, '使用经过验证的方法', 1), (66, 2, '尝试新的创新方法', 2),
(67, 1, '活在当下，关注现实', 1), (67, 2, '展望未来，关注可能性', 2),
(68, 1, '按部就班，遵循既定流程', 1), (68, 2, '灵活变通，追求创新', 2);

-- T/F 维度选项
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(69, 1, '逻辑和客观分析', 1), (69, 2, '个人价值观和对他人的影响', 2),
(70, 1, '帮助分析问题，提供解决方案', 1), (70, 2, '倾听和提供情感支持', 2),
(71, 1, '能力和成就', 1), (71, 2, '品格和动机', 2),
(72, 1, '找出正确的答案', 1), (72, 2, '维护和谐的关系', 2),
(73, 1, '公正和坚持原则', 1), (73, 2, '善良和体谅他人', 2),
(74, 1, '直接指出问题，就事论事', 1), (74, 2, '委婉表达，顾及对方感受', 2),
(75, 1, '追求真理，即使会伤害他人', 1), (75, 2, '维护和谐，即使要妥协真相', 2);

-- J/P 维度选项
INSERT INTO `test_option` (`question_id`, `option_order`, `content`, `score`) VALUES
(76, 1, '有计划、有条理', 1), (76, 2, '灵活、随性', 2),
(77, 1, '提前完成，留有余地', 1), (77, 2, '临近期限时效率最高', 2),
(78, 1, '整洁有序，物品各就各位', 1), (78, 2, '有些凌乱，但我知道东西在哪', 2),
(79, 1, '详细规划，确定每个步骤', 1), (79, 2, '保持开放，边做边调整', 2),
(80, 1, '提前规划行程和住宿', 1), (80, 2, '随机应变，说走就走', 2),
(81, 1, '喜欢稳定，不太喜欢变化', 1), (81, 2, '喜欢变化，觉得变化带来机会', 2),
(82, 1, '先完成工作，再放松娱乐', 1), (82, 2, '工作和娱乐可以穿插进行', 2);

-- MBTI结果区间（使用特殊计分方式）
-- E/I: 7-10为E, 11-14为I
-- S/N: 7-10为S, 11-14为N
-- T/F: 7-10为T, 11-14为F
-- J/P: 7-10为J, 11-14为P
-- 这里用总分简化判断：28-42偏外向实际型，43-56偏内向直觉型

INSERT INTO `test_result_range` (`paper_id`, `min_score`, `max_score`, `result_level`, `result_title`, `result_description`, `suggestion`) VALUES
(4, 28, 35, 'ESTJ', '执行者 ESTJ', '您倾向于外向(E)、感觉(S)、思考(T)、判断(J)。您是务实的组织者，喜欢掌控局面，注重效率和秩序。您善于制定计划并确保执行，是可靠的领导者。', '发挥您的组织能力，但也要学会倾听他人意见，给予他人更多灵活性。'),
(4, 36, 42, 'ISFJ', '守护者 ISFJ', '您倾向于内向(I)、感觉(S)、情感(F)、判断(J)。您是忠诚的守护者，关心他人，注重传统和责任。您善于照顾他人需求，是可靠的支持者。', '您的奉献精神很可贵，但也要记得关照自己的需求，学会说"不"。'),
(4, 43, 49, 'INFP', '调停者 INFP', '您倾向于内向(I)、直觉(N)、情感(F)、知觉(P)。您是理想主义者，富有创造力和同理心。您追求真实和意义，善于理解他人的感受。', '保持您的理想主义，但也要学会面对现实，将想法付诸行动。'),
(4, 50, 56, 'ENTP', '辩论家 ENTP', '您倾向于外向(E)、直觉(N)、思考(T)、知觉(P)。您是聪明的探索者，喜欢挑战传统，善于辩论和创新。您对新想法充满热情，思维敏捷。', '利用您的创造力和辩论技巧，但也要学会坚持完成项目，避免半途而废。');

SET FOREIGN_KEY_CHECKS = 1;
