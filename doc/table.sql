create schema if not exists practice_question;
use practice_question;
create table if not exists s_user
(
    id           bigint auto_increment comment 'id' primary key,
    user_account varchar(256)                          not null comment '账号',
    password     varchar(255)                          not null comment '加密密码',
    user_name    varchar(64)                           not null comment '昵称',
    email        varchar(128)                          null comment '用户邮箱（唯一）',
    avatar       varchar(512)                          null comment '用户头像',
    profile      text                                  null comment '用户简介',
    role         varchar(32) default 'user'            not null comment '用户角色：user/admin',
    edit_time    datetime    default CURRENT_TIMESTAMP not null comment '编辑时间',
    create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint     default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (user_account),
    INDEX idx_userName (user_name)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 题目表
create table if not exists question
(
    id          bigint auto_increment comment '主键 id' primary key,
    title       varchar(256)                       not null comment '标题',
    content     mediumtext                         not null comment '题目内容',
    tags        varchar(1024)                      null comment '标签列表（json 字符串）',
    answer      mediumtext                         not null comment '推荐答案',
    difficulty  int      default '0'               not null comment '题目难度：0-简单、1-中等、2-困难',
    thumb_num   int                                not null comment '点赞数',
    user_id     bigint                             not null comment '创建用户 id',
    edit_time   datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted  tinyint(1)                         not null default 0 comment '逻辑删除：0-否 1-是',
    -- 索引
    index idx_title (title),
    index idx_user_id (user_id)
) comment ='题目表' collate = utf8mb4_unicode_ci;

-- 题库表
create table if not exists question_bank
(
    id          bigint auto_increment primary key comment '主键 id',
    title       varchar(200)                       not null comment '题库标题',
    description text                               null comment '题库描述',
    cover_url   varchar(512)                       null comment '封面图片 URL',
    user_id     bigint                             not null comment '创建用户 id',
    edit_time   datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted  tinyint(1)                         not null default 0 comment '逻辑删除',
    index idx_title (title),
    index idx_user_id (user_id)
) comment ='题库表' collate = utf8mb4_unicode_ci;

-- 题库-题目关联表（硬删除）
create table if not exists question_bank_question
(
    id               bigint auto_increment primary key comment '主键 id',
    question_bank_id bigint                             not null comment '题库 id',
    question_id      bigint                             not null comment '题目 id',
    user_id          bigint                             not null comment '添加题目到题库的用户',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_question_bank_id (question_bank_id),
    index idx_question_id (question_id)
) comment ='题库题目关联表' collate = utf8mb4_unicode_ci;

-- 点赞记录表
create table if not exists thumb
(
    id          bigint auto_increment primary key comment '主键 id',
    user_id     bigint                             not null comment '用户 id',
    question_id bigint                             not null comment '题目 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    index idx_userId (user_id),
    index idx_questionId (question_id)
) comment ='点赞记录表';

-- 用户题目掌握状态表
create table if not exists user_question_status
(
    id          bigint auto_increment primary key comment '主键 id',
    user_id     bigint                             not null comment '用户 id',
    question_id bigint                             not null comment '题目 id',
    status      tinyint                            not null default 0 comment '掌握程度：0-未做 1-未掌握 2-一般 3-熟练',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '最近一次做题时间',
    index idx_user_id (user_id),
    index idx_question_id (question_id)
) comment ='用户题目掌握状态表' collate = utf8mb4_unicode_ci;


-- 练习题目表（用于检验问答题掌握程度）
create table if not exists question_practice
(
    id          bigint auto_increment comment '主键 id' primary key,
    question_id bigint       not null comment '问答题 id，关联 question 表',
    type        tinyint      not null comment '题型：0-单选 1-多选 2-判断',
    content     mediumtext   not null comment '题干',
    options     text         null comment '选项列表（json 字符串，业务层维护）',
    answer      varchar(128) not null comment '正确答案',
    explanation text         null comment '题目解析',
    sort_order  int          not null default 0 comment '排序顺序',
    create_time datetime              default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime              default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted  tinyint(1)   not null default 0 comment '逻辑删除：0-否 1-是',
    index idx_question_id (question_id),
    index idx_sort_order (sort_order)
) comment ='练习题目表' collate = utf8mb4_unicode_ci;

