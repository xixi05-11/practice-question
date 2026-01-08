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
)comment '用户' collate = utf8mb4_unicode_ci;

