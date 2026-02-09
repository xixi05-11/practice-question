package com.jie.practicequestions.domain.dto.questionpractice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 提交练习题答案请求
 */
@Data
public class QuestionPracticeSubmitRequest implements Serializable {

    /**
     * 练习题 id
     */
    private Long id;

    /**
     * 用户提交的答案
     * 单选/判断：单个选项，如 "A" 或 "true"
     * 多选：多个选项，如 "ABC" 或 "A,B,C"
     */
    private String userAnswer;

    @Serial
    private static final long serialVersionUID = 1L;
}