drop table
my_blog_db.tb_admin_user,
my_blog_db.tb_blog,
my_blog_db.tb_blog_category,
my_blog_db.tb_blog_comment,
my_blog_db.tb_blog_tag,
my_blog_db.tb_blog_tag_relation,
my_blog_db.tb_config,
my_blog_db.tb_link;

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`my_blog_db` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `my_blog_db`;

DROP TABLE IF EXISTS `tb_admin_user`;

CREATE TABLE `tb_admin_user` (
                                 `admin_user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员id',
                                 `login_user_name` varchar(50) NOT NULL COMMENT '管理员登陆名称',
                                 `login_password` varchar(50) NOT NULL COMMENT '管理员登陆密码',
                                 `nick_name` varchar(50) NOT NULL COMMENT '管理员显示昵称',
                                 `locked` tinyint(4) DEFAULT '0' COMMENT '是否锁定 0未锁定 1已锁定无法登陆',
                                 PRIMARY KEY (`admin_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert  into `tb_admin_user`(`admin_user_id`,`login_user_name`,`login_password`,`nick_name`,`locked`) values (1,'admin','e10adc3949ba59abbe56e057f20f883e','橙子',0);


DROP TABLE IF EXISTS `tb_blog`;

CREATE TABLE `tb_blog` (
                           `blog_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '博客表主键id',
                           `blog_title` varchar(200) NOT NULL COMMENT '博客标题',
                           `blog_sub_url` varchar(200) NOT NULL COMMENT '博客自定义路径url',
                           `blog_cover_image` varchar(200) NOT NULL COMMENT '博客封面图',
                           `blog_content` mediumtext NOT NULL COMMENT '博客内容',
                           `blog_category_id` int(11) NOT NULL COMMENT '博客分类id',
                           `blog_category_name` varchar(50) NOT NULL COMMENT '博客分类(冗余字段)',
                           `blog_tags` varchar(200) NOT NULL COMMENT '博客标签',
                           `blog_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-草稿 1-发布',
                           `blog_views` bigint(20) NOT NULL DEFAULT '0' COMMENT '阅读量',
                           `enable_comment` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-允许评论 1-不允许评论',
                           `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0=否 1=是',
                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                           PRIMARY KEY (`blog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tb_blog_category`;

CREATE TABLE `tb_blog_category` (
                                    `category_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类表主键',
                                    `category_name` varchar(50) NOT NULL COMMENT '分类的名称',
                                    `category_icon` varchar(50) NOT NULL COMMENT '分类的图标',
                                    `category_rank` int(11) NOT NULL DEFAULT '1' COMMENT '分类的排序值 被使用的越多数值越大',
                                    `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0=否 1=是',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tb_blog_comment`;

CREATE TABLE `tb_blog_comment` (
                                   `comment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                   `blog_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '关联的blog主键',
                                   `commentator` varchar(50) NOT NULL DEFAULT '' COMMENT '评论者名称',
                                   `email` varchar(100) NOT NULL DEFAULT '' COMMENT '评论人的邮箱',
                                   `website_url` varchar(50) NOT NULL DEFAULT '' COMMENT '网址',
                                   `comment_body` varchar(200) NOT NULL DEFAULT '' COMMENT '评论内容',
                                   `comment_create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论提交时间',
                                   `commentator_ip` varchar(20) NOT NULL DEFAULT '' COMMENT '评论时的ip地址',
                                   `reply_body` varchar(200) NOT NULL DEFAULT '' COMMENT '回复内容',
                                   `reply_create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
                                   `comment_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否审核通过 0-未审核 1-审核通过',
                                   `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除 0-未删除 1-已删除',
                                   PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tb_blog_tag`;

CREATE TABLE `tb_blog_tag` (
                               `tag_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标签表主键id',
                               `tag_name` varchar(100) NOT NULL COMMENT '标签名称',
                               `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0=否 1=是',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tb_blog_tag_relation`;

CREATE TABLE `tb_blog_tag_relation` (
                                        `relation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关系表id',
                                        `blog_id` bigint(20) NOT NULL COMMENT '博客id',
                                        `tag_id` int(11) NOT NULL COMMENT '标签id',
                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                                        PRIMARY KEY (`relation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tb_config`;

CREATE TABLE `tb_config` (
                             `config_name` varchar(100) NOT NULL DEFAULT '' COMMENT '配置项的名称',
                             `config_value` varchar(200) NOT NULL DEFAULT '' COMMENT '配置项的值',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                             PRIMARY KEY (`config_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `tb_config` */
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('footerAbout', '仅供分享使用', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('footerCopyRight', '2024 橙子', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('footerICP', '皖ICP备2025075088号-1', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('footerPoweredBy', 'https://github.com/ChengHaoRan666', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('footerPoweredByURL', 'https://github.com/ChengHaoRan666', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('websiteDescription', 'personal blog是SpringBoot2+Thymeleaf+Mybatis建造的个人博客网站.SpringBoot实战博客源码.个人博客搭建', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('websiteIcon', '/admin/dist/img/favicon.png', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('websiteLogo', '/admin/dist/img/logo2.png', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('websiteName', 'personal blog', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('yourAvatar', '/admin/dist/img/13.png', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('yourEmail', '2640708884@qq.com', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time) VALUES ('yourName', '橙子', '2024-12-01 20:33:23', '2024-12-01 20:33:23');


DROP TABLE IF EXISTS `tb_link`;

CREATE TABLE `tb_link` (
                           `link_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '友链表主键id',
                           `link_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '友链类别 0-友链 1-推荐 2-个人网站',
                           `link_name` varchar(50) NOT NULL COMMENT '网站名称',
                           `link_url` varchar(100) NOT NULL COMMENT '网站链接',
                           `link_description` varchar(100) NOT NULL COMMENT '网站描述',
                           `link_rank` int(11) NOT NULL DEFAULT '0' COMMENT '用于列表排序',
                           `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0-未删除 1-已删除',
                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                           PRIMARY KEY (`link_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
