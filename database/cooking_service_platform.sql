/*
Navicat MySQL Data Transfer

Source Server         : 密码：123456
Source Server Version : 50537
Source Host           : localhost:3306
Source Database       : cooking_service_platform

Target Server Type    : MYSQL
Target Server Version : 50537
File Encoding         : 65001

Date: 2026-07-05 07:35:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（加密）',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', '林深', '123456', '10000000000', '2026-06-02 19:14:36');

-- ----------------------------
-- Table structure for `chef`
-- ----------------------------
DROP TABLE IF EXISTS `chef`;
CREATE TABLE `chef` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '厨师ID',
  `name` varchar(50) NOT NULL COMMENT '厨师姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `skill` varchar(100) DEFAULT NULL COMMENT '擅长菜系',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '基础服务费',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `total_earnings` decimal(10,2) DEFAULT '0.00' COMMENT '总收入',
  `paid_amount` decimal(10,2) DEFAULT '0.00' COMMENT '已发放',
  `remaining_amount` decimal(10,2) DEFAULT '0.00' COMMENT '剩余',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='厨师表';

-- ----------------------------
-- Records of chef
-- ----------------------------
INSERT INTO `chef` VALUES ('1', '张厨师', '13900139000', '川菜', '200.00', '123456', '2026-04-14 23:40:19', '0.00', '0.00', '0.00');
INSERT INTO `chef` VALUES ('2', '李厨师', '13900139001', '粤菜', '250.00', '123456', '2026-04-14 23:40:19', '0.00', '0.00', '0.00');
INSERT INTO `chef` VALUES ('3', '王厨师', '13900139002', '淮扬菜', '180.00', '123456', '2026-04-14 23:40:19', '0.00', '0.00', '0.00');

-- ----------------------------
-- Table structure for `chef_level`
-- ----------------------------
DROP TABLE IF EXISTS `chef_level`;
CREATE TABLE `chef_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `chef_id` bigint(20) NOT NULL COMMENT '厨师ID',
  `level_code` int(11) NOT NULL COMMENT '等级编码：1-初级 2-中级 3-高级 4-技师 5-高级技师',
  PRIMARY KEY (`id`),
  KEY `idx_chef_id` (`chef_id`),
  CONSTRAINT `chef_level_ibfk_1` FOREIGN KEY (`chef_id`) REFERENCES `chef` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='厨师等级表';

-- ----------------------------
-- Records of chef_level
-- ----------------------------
INSERT INTO `chef_level` VALUES ('1', '1', '1');
INSERT INTO `chef_level` VALUES ('2', '2', '2');
INSERT INTO `chef_level` VALUES ('3', '3', '3');

-- ----------------------------
-- Table structure for `chef_skill`
-- ----------------------------
DROP TABLE IF EXISTS `chef_skill`;
CREATE TABLE `chef_skill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '技能ID',
  `chef_id` bigint(20) NOT NULL COMMENT '厨师ID',
  `skill_name` varchar(50) NOT NULL COMMENT '技能名称（如川菜、粤菜、淮扬菜等）',
  `level` tinyint(4) DEFAULT '1' COMMENT '技能等级（1-5）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_chef_id` (`chef_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='厨师技能表';

-- ----------------------------
-- Records of chef_skill
-- ----------------------------
INSERT INTO `chef_skill` VALUES ('1', '1', '川菜', '5', '2026-04-14 23:40:19');
INSERT INTO `chef_skill` VALUES ('2', '1', '湘菜', '3', '2026-04-14 23:40:19');
INSERT INTO `chef_skill` VALUES ('3', '2', '粤菜', '5', '2026-04-14 23:40:19');
INSERT INTO `chef_skill` VALUES ('4', '3', '海鲜', '4', '2026-04-14 23:40:19');
INSERT INTO `chef_skill` VALUES ('5', '2', '淮扬菜', '5', '2026-04-14 23:40:19');
INSERT INTO `chef_skill` VALUES ('6', '3', '本帮菜', '4', '2026-04-14 23:40:19');

-- ----------------------------
-- Table structure for `chef_status`
-- ----------------------------
DROP TABLE IF EXISTS `chef_status`;
CREATE TABLE `chef_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `chef_id` bigint(20) NOT NULL COMMENT '厨师ID',
  `status_date` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日期（格式：YYYY-MM-DD）',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-空闲 1-忙碌（默认忙碌）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_chef_date` (`chef_id`,`status_date`) COMMENT '厨师ID+日期唯一索引',
  KEY `idx_chef_id` (`chef_id`) COMMENT '厨师ID索引',
  KEY `idx_status_date` (`status_date`) COMMENT '日期索引'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='厨师状态表（以日期为单位）';

-- ----------------------------
-- Records of chef_status
-- ----------------------------

-- ----------------------------
-- Table structure for `dish`
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `name` varchar(100) NOT NULL COMMENT '菜品名称',
  `price` decimal(10,2) NOT NULL COMMENT '菜品价格',
  `description` text COMMENT '菜品描述',
  `spiciness` tinyint(4) DEFAULT '0' COMMENT '辣度等级（1-5）',
  `ingredients` varchar(500) DEFAULT NULL COMMENT '主要配料',
  `cuisine` varchar(50) DEFAULT NULL COMMENT '所属菜系',
  `image` varchar(255) DEFAULT NULL COMMENT '菜品图片',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- ----------------------------
-- Records of dish
-- ----------------------------
INSERT INTO `dish` VALUES ('2', '红烧肉', '30.00', '肥而不腻，入口即化1', '0', '五花肉、冰糖、酱油', '川菜', '/src/assets/dish-hongshaorou.png', '2026-04-14 23:40:19');
INSERT INTO `dish` VALUES ('3', '清蒸鱼', '2.00', '1', '0', '1', '粤菜', '/src/assets/dish-qingzhengyu.png', '2026-04-14 23:40:19');
INSERT INTO `dish` VALUES ('4', '辣椒炒肉', '23.00', '椒香浓郁，肉嫩下饭', '4', '猪肉、青椒、豆鼓', '淮扬菜', '/src/assets/dish-lajiaochaorou.png', '2026-05-11 23:39:21');

-- ----------------------------
-- Table structure for `order`
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `chef_id` bigint(20) NOT NULL COMMENT '厨师ID',
  `cooking_time` datetime NOT NULL COMMENT '上门烹饪时间',
  `address` varchar(255) NOT NULL COMMENT '烹饪地址',
  `price` decimal(10,2) NOT NULL COMMENT '订单金额',
  `status` tinyint(4) DEFAULT '0' COMMENT '订单状态（0-待支付，1-已支付，2-已完成，3-已取消）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_chef_id` (`chef_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Records of order
-- ----------------------------

-- ----------------------------
-- Table structure for `order_item`
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `dish_id` bigint(20) NOT NULL COMMENT '菜品ID',
  `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_dish_id` (`dish_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ----------------------------
-- Records of order_item
-- ----------------------------

-- ----------------------------
-- Table structure for `payment`
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `status` tinyint(4) DEFAULT '0' COMMENT '支付状态（0-待支付，1-已支付，2-支付失败）',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '交易流水号',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ----------------------------
-- Records of payment
-- ----------------------------

-- ----------------------------
-- Table structure for `recharge_record`
-- ----------------------------
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0-待确认，1-已确认，2-已拒绝）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='充值记录表';

-- ----------------------------
-- Records of recharge_record
-- ----------------------------

-- ----------------------------
-- Table structure for `review`
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `rating` int(11) NOT NULL COMMENT '评分（1-5）',
  `content` text COMMENT '评价内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ----------------------------
-- Records of review
-- ----------------------------

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（加密）',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `address` varchar(255) DEFAULT NULL COMMENT '常用地址',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '测试用户', '123456', '13800138000', '北京市朝阳区测试路123号', '0.00', '2026-04-14 23:40:19');
