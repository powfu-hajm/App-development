-- 1. 检查当前表结构
DESCRIBE article;

-- 2. 如果缺少 type 字段，添加它
ALTER TABLE article ADD COLUMN type VARCHAR(50) DEFAULT NULL COMMENT '文章类型/分类，如 专栏/测试/科普';

-- 3. 如果存在其他问题，可以重新创建表
DROP TABLE IF EXISTS article;
CREATE TABLE `article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) NOT NULL COMMENT '文章标题',
  `summary` text COMMENT '文章简介/摘要',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图片URL',
  `original_url` varchar(500) NOT NULL COMMENT '原文链接',
  `source` varchar(50) DEFAULT '简单心理' COMMENT '来源/站点',
  `type` varchar(50) DEFAULT NULL COMMENT '文章类型/分类，如 专栏/测试/科普',
  `read_count` int(11) DEFAULT 0 COMMENT '阅读量(虚拟）',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间(爬取自原文)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '抓取入库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_original_url` (`original_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心理文章表';