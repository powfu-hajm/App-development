-- 创建用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(加密)',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- (可选) 插入初始 ROOT 用户，密码是 'root' 的 MD5 值 (假设使用的是标准MD5)
-- 密码 'root' 的 MD5 是: 63a9f0ea7bb98050796b649e85481845
INSERT INTO `user` (`username`, `password`, `nickname`) VALUES ('root', '63a9f0ea7bb98050796b649e85481845', '超级管理员');

