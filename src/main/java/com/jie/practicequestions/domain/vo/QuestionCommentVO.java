package com.jie.practicequestions.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目评论 VO
 */
@Data
public class QuestionCommentVO implements Serializable {

    /**
     * 评论 id
     */
    private Long id;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论 id
     */
    private Long parentId;

    /**
     * 根评论 id
     */
    private Long rootId;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 评论用户信息
     */
    private UserVO userVO;

    /**
     * 被回复用户信息
     */
    private UserVO replyUserVO;

    /**
     * 是否点赞
     */
    private Boolean hasThumb;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子评论列表（仅用于一级评论）
     */
    private List<QuestionCommentVO> children;

    /**
     * 子评论总数（用于显示"查看更多XX条回复"）
     */
    private Long childrenCount;

    @Serial
    private static final long serialVersionUID = 1L;
}