package com.jie.practicequestions.domain.dto.questioncomment;

import com.jie.practicequestions.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 评论查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionCommentQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 根评论 id（用于查询某条一级评论下的所有回复）
     */
    private Long rootId;

    /**
     * 评论用户 id
     */
    private Long userId;

    /**
     * 每条根评论包含的子评论数量（默认3条，-1表示全部）
     */
    private Integer childrenLimit = 3;

    @Serial
    private static final long serialVersionUID = 1L;
}