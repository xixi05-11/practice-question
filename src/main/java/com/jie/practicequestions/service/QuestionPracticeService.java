package com.jie.practicequestions.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jie.practicequestions.domain.dto.questionpractice.QuestionPracticeQueryRequest;
import com.jie.practicequestions.domain.dto.questionpractice.QuestionPracticeSubmitRequest;
import com.jie.practicequestions.domain.model.QuestionPractice;
import com.jie.practicequestions.domain.vo.QuestionPracticeCheckResultVO;
import com.jie.practicequestions.domain.vo.QuestionPracticeVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author 杰
 * @description 针对表【question_practice(练习题目表)】的数据库操作Service
 * @createDate 2026-01-31 22:23:16
 */
public interface QuestionPracticeService extends IService<QuestionPractice> {

    /**
     * 获取练习题封装
     *
     * @param questionPractice 练习题实体
     * @param request          HTTP请求
     * @return 练习题VO
     */
    QuestionPracticeVO getQuestionPracticeVO(QuestionPractice questionPractice, HttpServletRequest request);

    /**
     * 获取练习题列表查询条件
     *
     * @param questionPracticeQueryRequest 查询请求
     * @return 查询条件
     */
    LambdaQueryWrapper<QuestionPractice> getQueryWrapper(QuestionPracticeQueryRequest questionPracticeQueryRequest);

    /**
     * 校验练习题答案
     *
     * @param submitRequest 提交答案请求
     * @return 校验结果
     */
    QuestionPracticeCheckResultVO checkAnswer(QuestionPracticeSubmitRequest submitRequest);
}
