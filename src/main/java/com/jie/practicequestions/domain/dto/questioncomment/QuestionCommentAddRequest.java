package com.jie.practicequestions.domain.dto.questioncomment;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 添加评论请求
 */
@Data
public class QuestionCommentAddRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论 id（回复评论时必填）
     */
    private Long parentId;

    /**
     * 被回复的用户 id（回复评论时必填）
     */
    private Long replyUserId;

    @Serial
    private static final long serialVersionUID = 1L;
}