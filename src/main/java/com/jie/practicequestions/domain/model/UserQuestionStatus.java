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
 * 用户题目掌握状态表
 * @TableName user_question_status
 */
@TableName(value ="user_question_status")
@Data
public class UserQuestionStatus implements Serializable {
    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 题目 id
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 掌握程度：0-未做 1-未掌握 2-一般 3-熟练
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 最近一次做题时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}