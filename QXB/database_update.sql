-- =====================================================
-- QXB 数据库增量更新脚本
-- 用于已有数据库补全缺失的表和字段
-- 安全执行：不会删除现有数据
-- =====================================================

USE `mood_diary`;

-- =====================================================
-- 1. 补全 user 表字段
-- =====================================================
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = 'mood_diary' AND table_name = 'user' AND column_name = 'phone');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE user ADD COLUMN phone VARCHAR(20) DEFAULT NULL COMMENT ''手机号'' AFTER avatar',
    'SELECT ''user.phone already exists'' as status');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- 2. 补全 diary 表字段
-- =====================================================
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = 'mood_diary' AND table_name = 'diary' AND column_name = 'deleted');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE diary ADD COLUMN deleted INT DEFAULT 0 COMMENT ''软删除标志''',
    'SELECT ''diary.deleted already exists'' as status');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- 3. 补全 article 表字段
-- =====================================================
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = 'mood_diary' AND table_name = 'article' AND column_name = 'type');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE article ADD COLUMN type VARCHAR(50) DEFAULT NULL COMMENT ''文章类型'' AFTER source',
    'SELECT ''article.type already exists'' as status');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- 4. 补全 test_option 表字段 (MBTI维度)
-- =====================================================
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = 'mood_diary' AND table_name = 'test_option' AND column_name = 'dimension');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE test_option ADD COLUMN dimension VARCHAR(10) DEFAULT NULL COMMENT ''MBTI维度'' AFTER score',
    'SELECT ''test_option.dimension already exists'' as status');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- 5. 创建 mbti_type 表 (如果不存在)
-- =====================================================
CREATE TABLE IF NOT EXISTS `mbti_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `type_code` VARCHAR(4) NOT NULL,
    `type_name` VARCHAR(50) NOT NULL,
    `type_name_en` VARCHAR(50) NOT NULL,
    `type_group` VARCHAR(20) NOT NULL,
    `brief_desc` VARCHAR(200) NOT NULL,
    `detail_desc` TEXT NOT NULL,
    `strengths` TEXT NOT NULL,
    `weaknesses` TEXT NOT NULL,
    `career_suggestions` TEXT,
    `famous_people` TEXT,
    `image_name` VARCHAR(100),
    `color_primary` VARCHAR(20) DEFAULT '#7C4DFF',
    `color_secondary` VARCHAR(20) DEFAULT '#B388FF',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MBTI人格类型表';

-- =====================================================
-- 6. 插入 MBTI 类型数据 (如果为空)
-- =====================================================
INSERT IGNORE INTO mbti_type (type_code, type_name, type_name_en, type_group, brief_desc, detail_desc, strengths, weaknesses, career_suggestions, famous_people, image_name, color_primary, color_secondary) VALUES
('INTJ', '建筑师', 'The Architect', '分析家', '富有想象力和战略性的思想家', 'INTJ是天生的战略家。', '["独立思考","战略规划","追求卓越"]', '["可能显得冷漠","要求过高"]', '["科学家","工程师","架构师"]', '["埃隆·马斯克","特斯拉"]', 'ic_mbti_intj', '#7C4DFF', '#B388FF'),
('INTP', '逻辑学家', 'The Logician', '分析家', '具有创造力的发明家', 'INTP是天生的思想家。', '["逻辑分析强","创造性思维","求知欲旺"]', '["过于理论化","社交较弱"]', '["程序员","数学家","研究员"]', '["爱因斯坦","比尔盖茨"]', 'ic_mbti_intp', '#7C4DFF', '#B388FF'),
('ENTJ', '指挥官', 'The Commander', '分析家', '意志强大的领导者', 'ENTJ是天生的领导者。', '["领导才能","战略眼光","决策果断"]', '["可能专横","不善情感"]', '["CEO","企业家","律师"]', '["乔布斯","撒切尔"]', 'ic_mbti_entj', '#7C4DFF', '#B388FF'),
('ENTP', '辩论家', 'The Debater', '分析家', '聪明好奇的思想家', 'ENTP是充满灵感的创新者。', '["思维敏捷","创新能力","善于辩论"]', '["过于争论","容易无聊"]', '["律师","创业者","产品经理"]', '["富兰克林","爱迪生"]', 'ic_mbti_entp', '#7C4DFF', '#B388FF'),
('INFJ', '提倡者', 'The Advocate', '外交家', '安静神秘的理想主义者', 'INFJ是理想主义者。', '["洞察力强","价值观坚定","富有同理心"]', '["过于理想化","容易疲惫"]', '["心理咨询师","作家","社工"]', '["马丁路德金","甘地"]', 'ic_mbti_infj', '#00BFA5', '#64FFDA'),
('INFP', '调停者', 'The Mediator', '外交家', '诗意善良的利他主义者', 'INFP是真正的理想主义者。', '["富有同情心","创造力丰富","开放包容"]', '["过于理想化","容易受伤"]', '["作家","艺术家","心理师"]', '["莎士比亚","列侬"]', 'ic_mbti_infp', '#00BFA5', '#64FFDA'),
('ENFJ', '主人公', 'The Protagonist', '外交家', '鼓舞人心的领导者', 'ENFJ是天生的领袖。', '["领导力出色","善于沟通","富有同理心"]', '["过于理想化","忽视自己"]', '["教师","HR经理","公关"]', '["奥巴马","奥普拉"]', 'ic_mbti_enfj', '#00BFA5', '#64FFDA'),
('ENFP', '竞选者', 'The Campaigner', '外交家', '热情有创造力的自由精神', 'ENFP是真正的自由精神。', '["热情感染力","创造力","善于沟通"]', '["容易分心","过于情绪化"]', '["演员","记者","营销"]', '["威尔史密斯"]', 'ic_mbti_enfp', '#00BFA5', '#64FFDA'),
('ISTJ', '物流师', 'The Logistician', '守护者', '实际且注重事实', 'ISTJ是可靠负责的人。', '["可靠负责","注重细节","组织能力强"]', '["可能固执","不善表达"]', '["会计师","审计师","项目经理"]', '["华盛顿","巴菲特"]', 'ic_mbti_istj', '#FF6F00', '#FFD54F'),
('ISFJ', '守卫者', 'The Defender', '守护者', '温暖的守护者', 'ISFJ是温暖体贴的人。', '["关怀他人","可靠耐心","注重细节"]', '["过于谦逊","难接受批评"]', '["护士","教师","社工"]', '["碧昂丝","凯特王妃"]', 'ic_mbti_isfj', '#FF6F00', '#FFD54F'),
('ESTJ', '执行者', 'The Executive', '守护者', '出色的管理者', 'ESTJ是组织者和执行者。', '["组织能力","执行力强","可靠负责"]', '["可能固执","不善情感"]', '["经理","法官","军官"]', '["亨利福特"]', 'ic_mbti_estj', '#FF6F00', '#FFD54F'),
('ESFJ', '执政官', 'The Consul', '守护者', '关心他人善于社交', 'ESFJ是热情关怀的人。', '["善于社交","关怀他人","组织能力"]', '["在意他人看法","可能控制"]', '["销售","护士","教师"]', '["泰勒斯威夫特"]', 'ic_mbti_esfj', '#FF6F00', '#FFD54F'),
('ISTP', '鉴赏家', 'The Virtuoso', '探险家', '大胆实际的实验家', 'ISTP是天生的问题解决者。', '["动手能力","冷静理性","解决问题"]', '["可能冷漠","承诺困难"]', '["工程师","机械师","飞行员"]', '["乔丹","布鲁斯李"]', 'ic_mbti_istp', '#FFD600', '#FFFF8D'),
('ISFP', '探险家', 'The Adventurer', '探险家', '灵活有魅力的艺术家', 'ISFP是温和敏感的人。', '["艺术天赋","温和友善","活在当下"]', '["过于敏感","逃避冲突"]', '["艺术家","设计师","摄影师"]', '["迈克尔杰克逊"]', 'ic_mbti_isfp', '#FFD600', '#FFFF8D'),
('ESTP', '企业家', 'The Entrepreneur', '探险家', '聪明精力充沛', 'ESTP是活力四射的人。', '["行动力强","善于社交","适应性强"]', '["可能冲动","不善规划"]', '["销售","企业家","演员"]', '["特朗普","麦当娜"]', 'ic_mbti_estp', '#FFD600', '#FFFF8D'),
('ESFP', '表演者', 'The Entertainer', '探险家', '精力充沛的表演者', 'ESFP是热情友好的人。', '["热情活泼","善于社交","乐观积极"]', '["追求享乐","规划能力弱"]', '["演员","销售","活动策划"]', '["玛丽莲梦露"]', 'ic_mbti_esfp', '#FFD600', '#FFFF8D');

-- =====================================================
-- 7. 更新 MBTI 选项维度标识
-- =====================================================
UPDATE test_option SET dimension = 'E' WHERE question_id IN (55,56,57,58,59,60,61) AND score = 1 AND dimension IS NULL;
UPDATE test_option SET dimension = 'I' WHERE question_id IN (55,56,57,58,59,60,61) AND score = 2 AND dimension IS NULL;
UPDATE test_option SET dimension = 'S' WHERE question_id IN (62,63,64,65,66,67,68) AND score = 1 AND dimension IS NULL;
UPDATE test_option SET dimension = 'N' WHERE question_id IN (62,63,64,65,66,67,68) AND score = 2 AND dimension IS NULL;
UPDATE test_option SET dimension = 'T' WHERE question_id IN (69,70,71,72,73,74,75) AND score = 1 AND dimension IS NULL;
UPDATE test_option SET dimension = 'F' WHERE question_id IN (69,70,71,72,73,74,75) AND score = 2 AND dimension IS NULL;
UPDATE test_option SET dimension = 'J' WHERE question_id IN (76,77,78,79,80,81,82) AND score = 1 AND dimension IS NULL;
UPDATE test_option SET dimension = 'P' WHERE question_id IN (76,77,78,79,80,81,82) AND score = 2 AND dimension IS NULL;

-- =====================================================
-- 完成
-- =====================================================
SELECT '数据库更新完成' as status;
