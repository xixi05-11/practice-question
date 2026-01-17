package com.jie.practicequestions.domain.vo;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.practicequestions.domain.model.QuestionBank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 题库表查询封装类
 */
@Data
public class QuestionBankVO implements Serializable {

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
     * 创建用户
     */
    private UserVO userVO;

    /**
     * 题目列表
     */
    private List<QuestionVO> questionVOList;

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

    /**
     * 对象转包装类
     *
     * @param questionBank
     * @return
     */
    public static QuestionBankVO objToVO(QuestionBank questionBank, UserVO userVO) {
        QuestionBankVO questionBankVO = new QuestionBankVO();
        BeanUtil.copyProperties(questionBank, questionBankVO);
        questionBankVO.setUserVO(userVO);
        return questionBankVO;
    }
}