/*
 Navicat Premium Data Transfer

 Source Server         : Local
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : localhost:3306
 Source Schema         : mood_diary

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 25/11/2025 02:04:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for diary
-- ----------------------------
DROP TABLE IF EXISTS `diary`;
CREATE TABLE `diary`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日记内容',
  `mood_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '心情标签',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diary
-- ----------------------------
INSERT INTO `diary` VALUES (1, 1, '开心', '甜蜜,压抑', '2025-11-24 22:42:07', '2025-11-24 23:17:47', 0);
INSERT INTO `diary` VALUES (2, 1, '难过', '悠闲,生气', '2025-11-24 22:44:30', '2025-11-24 23:17:51', 0);
INSERT INTO `diary` VALUES (3, 1, '一般', '甜蜜,冲鸭,焦虑,压抑,难过', '2025-11-24 22:44:48', '2025-11-24 23:20:54', 0);
INSERT INTO `diary` VALUES (5, 1, '郁闷', '冲鸭,疲惫', '2025-11-24 23:19:30', '2025-11-25 00:48:10', 0);
INSERT INTO `diary` VALUES (6, 1, '伤心', '开心,甜蜜', '2025-11-25 00:07:05', '2025-11-25 00:47:31', 0);

SET FOREIGN_KEY_CHECKS = 1;
