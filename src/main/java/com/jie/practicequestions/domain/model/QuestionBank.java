package com.jie.practicequestions.domain.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 题库表
 * @TableName question_bank
 */
@TableName(value ="question_bank")
@Data
public class QuestionBank implements Serializable {
    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题库标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 题库描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 封面图片 URL
     */
    @TableField(value = "cover_url")
    private String coverUrl;

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
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}