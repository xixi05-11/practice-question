package com.jie.practicequestions.domain.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 题目表
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable {
    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 题目内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 标签列表（json 字符串）
     */
    @TableField(value = "tags")
    private String tags;

    /**
     * 推荐答案
     */
    @TableField(value = "answer")
    private String answer;

    /**
     * 题目难度：0-简单、1-中等、2-困难
     */
    @TableField(value = "difficulty")
    private Integer difficulty;

    /**
     * 点赞数
     */
    @TableField(value = "thumb_num")
    private Integer thumbNum;

    /**
     * 创建用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 编辑时间
     */
    @TableField(value = "edit_time")
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-否 1-是
     */
    @TableLogic
    private Integer isDeleted;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}