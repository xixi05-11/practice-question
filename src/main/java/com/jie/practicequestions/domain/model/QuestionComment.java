package com.jie.practicequestions.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 题目评论表
 * @TableName question_comment
 */
@TableName(value ="question_comment")
@Data
public class QuestionComment implements Serializable {
    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 题目 id
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 评论用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 评论内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 父评论 id（一级评论为 null，二级评论为父评论 id）
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 根评论 id（一级评论为 null，二级评论指向最上层一级评论）
     */
    @TableField(value = "root_id")
    private Long rootId;

    /**
     * 被回复的用户 id（用于显示"回复 @xxx"）
     */
    @TableField(value = "reply_user_id")
    private Long replyUserId;

    /**
     * 点赞数
     */
    @TableField(value = "thumb_num")
    private Integer thumbNum;

    /**
     * 逻辑删除：0-否 1-是
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}