package com.jie.practicequestions.domain.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 练习题目表
 * @TableName question_practice
 */
@TableName(value ="question_practice")
@Data
public class QuestionPractice implements Serializable {
    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 问答题 id，关联 question 表
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 题型：0-单选 1-多选 2-判断
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 题干
     */
    @TableField(value = "content")
    private String content;

    /**
     * 选项列表（json 字符串，业务层维护）
     */
    @TableField(value = "options")
    private String options;

    /**
     * 正确答案
     */
    @TableField(value = "answer")
    private String answer;

    /**
     * 题目解析
     */
    @TableField(value = "explanation")
    private String explanation;

    /**
     * 排序顺序
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

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