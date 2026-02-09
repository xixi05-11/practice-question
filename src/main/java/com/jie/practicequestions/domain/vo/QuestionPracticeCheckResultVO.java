package com.jie.practicequestions.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 练习题答案校验结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPracticeCheckResultVO implements Serializable {

    /**
     * 练习题 id
     */
    private Long questionPracticeId;

    /**
     * 是否回答正确
     */
    private Boolean isCorrect;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 答案解析 0
     */
    private String explanation;

    @Serial
    private static final long serialVersionUID = 1L;
}