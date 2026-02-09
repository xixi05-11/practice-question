package com.jie.practicequestions.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 练习题视图对象
 */
@Data
public class QuestionPracticeVO implements Serializable {

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
     * 选项列表（json 字符串）
     */
    private String options;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}