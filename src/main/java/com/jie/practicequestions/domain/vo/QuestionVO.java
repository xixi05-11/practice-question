package com.jie.practicequestions.domain.vo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.jie.practicequestions.domain.enums.QuestionDiffEnum;
import com.jie.practicequestions.domain.model.Question;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目表查询封装类
 */
@Data
public class QuestionVO implements Serializable {

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
     * 推荐答案
     */
    private String answer;

    /**
     * 难度
     */
    private Integer difficulty;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 创建用户
     */
    private UserVO userVO;

    /**
     * 掌握程度
     */
    private Integer status;

    /**
     * 是否点赞
     */
    private Boolean hasThumb;

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