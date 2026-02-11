package com.jie.practicequestions.domain.dto.questionbank;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建题库请求
 */
@Data
public class QuestionBankAddRequest implements Serializable {

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