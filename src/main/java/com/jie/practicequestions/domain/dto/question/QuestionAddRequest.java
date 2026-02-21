package com.jie.practicequestions.domain.dto.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 创建题目请求
 */
@Data
public class QuestionAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 推荐答案
     */
    private String answer;

    /**
     * 难度
     */
    private Integer difficulty;

    /**
     * 所属题库 id 列表
     */
    private List<Long> questionBankIdList;

    @Serial
    private static final long serialVersionUID = 1L;
}