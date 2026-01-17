package com.jie.practicequestions.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jie.practicequestions.domain.dto.question.QuestionQueryRequest;
import com.jie.practicequestions.domain.dto.questionbank.QuestionBankQueryRequest;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.model.QuestionBank;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 杰
* @description 针对表【question_bank(题库表)】的数据库操作Service
* @createDate 2026-01-15 18:12:19
*/
public interface QuestionBankService extends IService<QuestionBank> {

    /**
     * 获取题目列表查询条件
     *
     * @param questionBankQueryRequest
     * @return
     */
    LambdaQueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest);
}
