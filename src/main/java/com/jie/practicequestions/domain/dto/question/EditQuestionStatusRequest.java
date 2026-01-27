package com.jie.practicequestions.domain.dto.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EditQuestionStatusRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 状态
     */
    private Integer status;
}
