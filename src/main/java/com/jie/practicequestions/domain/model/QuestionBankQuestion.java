package com.jie.practicequestions.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 题库题目关联表
 * @TableName question_bank_question
 */
@TableName(value ="question_bank_question")
@Data
public class QuestionBankQuestion implements Serializable {
    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题库 id
     */
    @TableField(value = "question_bank_id")
    private Long questionBankId;

    /**
     * 题目 id
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 添加题目到题库的用户
     */
    @TableField(value = "user_id")
    private Long userId;

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

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}