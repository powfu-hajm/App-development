-- 聊天记录表
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role` varchar(10) NOT NULL COMMENT '角色: user 或 assistant',
  `content` text NOT NULL COMMENT '聊天内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天记录表';

