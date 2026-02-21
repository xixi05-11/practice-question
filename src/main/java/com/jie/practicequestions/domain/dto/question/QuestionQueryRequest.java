package com.jie.practicequestions.domain.dto.question;

import com.jie.practicequestions.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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
     * 题目难度
     */
    private Integer difficulty;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 编辑时间
     */
    private LocalDateTime startEditTime;

    /**
     * 编辑时间
     */
    private LocalDateTime endEditTime;

    /**
     * 创建时间
     */
    private LocalDateTime startCreateTime;
    /**
     * 创建时间
     */
    private LocalDateTime endCreateTime;

    /**
     * 题库 id
     */
    private Long questionBankId;

}
