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
 * 点赞记录表
 * @TableName thumb
 */
@TableName(value ="thumb")
@Data
public class Thumb implements Serializable {
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
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}