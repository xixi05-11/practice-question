package com.jie.practicequestions.domain.dto.questionpractice;

import com.jie.practicequestions.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 练习题查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionPracticeQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 问答题 id
     */
    private Long questionId;

    /**
     * 题型：0-单选 1-多选 2-判断
     */
    private Integer type;

    /**
     * 题干
     */
    private String content;

    /**
     * 创建时间开始
     */
    private LocalDateTime startCreateTime;

    /**
     * 创建时间结束
     */
    private LocalDateTime endCreateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}