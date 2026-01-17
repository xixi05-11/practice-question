package com.jie.practicequestions.domain.dto.questionbank;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jie.practicequestions.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键 id
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

}
