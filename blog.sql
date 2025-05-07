create table my_blog_db.tb_admin_user
(
    admin_user_id   int auto_increment comment '管理员id'
        primary key,
    login_user_name varchar(50)       not null comment '管理员登陆名称',
    login_password  varchar(50)       not null comment '管理员登陆密码',
    nick_name       varchar(50)       not null comment '管理员显示昵称',
    locked          tinyint default 0 null comment '是否锁定 0未锁定 1已锁定无法登陆'
)
    charset = utf8mb4;

create table my_blog_db.tb_blog
(
    blog_id            bigint auto_increment comment '博客表主键id'
        primary key,
    blog_title         varchar(200)                       not null comment '博客标题',
    blog_sub_url       varchar(200)                       not null comment '博客自定义路径url',
    blog_cover_image   varchar(200)                       not null comment '博客封面图',
    blog_content       mediumtext                         not null comment '博客内容',
    blog_category_id   int                                not null comment '博客分类id',
    blog_category_name varchar(50)                        not null comment '博客分类(冗余字段)',
    blog_tags          varchar(200)                       not null comment '博客标签',
    blog_status        tinyint  default 0                 not null comment '0-草稿 1-发布',
    blog_views         bigint   default 0                 not null comment '阅读量',
    enable_comment     tinyint  default 0                 not null comment '0-允许评论 1-不允许评论',
    is_deleted         tinyint  default 0                 not null comment '是否删除 0=否 1=是',
    create_time        datetime default CURRENT_TIMESTAMP not null comment '添加时间',
    update_time        datetime default CURRENT_TIMESTAMP null comment '修改时间'
)
    charset = utf8mb4;

create table my_blog_db.tb_blog_category
(
    category_id   int auto_increment comment '分类表主键'
        primary key,
    category_name varchar(50)                        not null comment '分类的名称',
    category_icon varchar(50)                        not null comment '分类的图标',
    category_rank int      default 1                 not null comment '分类的排序值 被使用的越多数值越大',
    is_deleted    tinyint  default 0                 not null comment '是否删除 0=否 1=是',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    charset = utf8mb4;

create table my_blog_db.tb_blog_comment
(
    comment_id          bigint auto_increment comment '主键id'
        primary key,
    blog_id             bigint       default 0                 not null comment '关联的blog主键',
    commentator         varchar(50)  default ''                not null comment '评论者名称',
    email               varchar(100) default ''                not null comment '评论人的邮箱',
    website_url         varchar(50)  default ''                not null comment '网址',
    comment_body        varchar(200) default ''                not null comment '评论内容',
    comment_create_time datetime     default CURRENT_TIMESTAMP not null comment '评论提交时间',
    commentator_ip      varchar(20)  default ''                not null comment '评论时的ip地址',
    reply_body          varchar(200) default ''                not null comment '回复内容',
    reply_create_time   datetime     default CURRENT_TIMESTAMP not null comment '回复时间',
    comment_status      tinyint      default 0                 not null comment '是否审核通过 0-未审核 1-审核通过',
    is_deleted          tinyint      default 0                 null comment '是否删除 0-未删除 1-已删除'
)
    charset = utf8mb4;

create table my_blog_db.tb_blog_tag
(
    tag_id      int auto_increment comment '标签表主键id'
        primary key,
    tag_name    varchar(100)                       not null comment '标签名称',
    is_deleted  tinyint  default 0                 not null comment '是否删除 0=否 1=是',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    charset = utf8mb4;

create table my_blog_db.tb_blog_tag_relation
(
    relation_id bigint auto_increment comment '关系表id'
        primary key,
    blog_id     bigint                             not null comment '博客id',
    tag_id      int                                not null comment '标签id',
    create_time datetime default CURRENT_TIMESTAMP null comment '添加时间'
)
    charset = utf8mb4;

create table my_blog_db.tb_config
(
    config_name  varchar(100) default ''                not null comment '配置项的名称'
        primary key,
    config_value varchar(200) default ''                not null comment '配置项的值',
    create_time  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime     default CURRENT_TIMESTAMP not null comment '修改时间'
)
    charset = utf8mb4;

create table my_blog_db.tb_link
(
    link_id          int auto_increment comment '友链表主键id'
        primary key,
    link_type        tinyint  default 0                 not null comment '友链类别 0-友链 1-推荐 2-个人网站',
    link_name        varchar(50)                        not null comment '网站名称',
    link_url         varchar(100)                       not null comment '网站链接',
    link_description varchar(100)                       not null comment '网站描述',
    link_rank        int      default 0                 not null comment '用于列表排序',
    is_deleted       tinyint  default 0                 not null comment '是否删除 0-未删除 1-已删除',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '添加时间'
)
    charset = utf8mb4;

INSERT INTO my_blog_db.tb_admin_user (admin_user_id, login_user_name, login_password, nick_name, locked)
VALUES (1, 'admin', '31528198109743225ff9d0cf04d1fdd1', '橙子', 0);

INSERT INTO my_blog_db.tb_config (config_name, config_value, create_time, update_time)
VALUES ('footerAbout', '仅供分享使用', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('footerCopyRight', '2024 橙子', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('footerICP', '皖ICP备2025075088号-1', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('footerPoweredBy', 'https://github.com/ChengHaoRan666', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('footerPoweredByURL', 'https://github.com/ChengHaoRan666', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('websiteDescription',
        'personal blog是SpringBoot2+Thymeleaf+Mybatis建造的个人博客网站.SpringBoot实战博客源码.个人博客搭建',
        '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('websiteIcon', '/admin/dist/img/favicon.png', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('websiteLogo', '/admin/dist/img/logo2.png', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('websiteName', 'personal blog', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('yourAvatar', '/admin/dist/img/13.png', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('yourEmail', '2640708884@qq.com', '2024-12-01 20:33:23', '2024-12-01 20:33:23')
     , ('yourName', '橙子', '2024-12-01 20:33:23', '2024-12-01 20:33:23');
