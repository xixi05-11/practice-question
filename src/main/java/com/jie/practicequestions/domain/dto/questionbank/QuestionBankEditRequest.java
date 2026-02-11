package com.jie.practicequestions.domain.dto.questionbank;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改题库请求
 */
@Data
public class QuestionBankEditRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long id;

    /**
     * 题库标题
     */
    private String title;

    /**
     * 题库描述
     */
    private String description;

    /**
     * 封面图片 URL
     */
    private String coverUrl;

    @Serial
    private static final long serialVersionUID = 1L;
}