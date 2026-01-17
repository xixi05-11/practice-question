package com.jie.practicequestions.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jie.practicequestions.domain.dto.question.QuestionQueryRequest;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.vo.QuestionVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author 杰
 * @description 针对表【question(题目表)】的数据库操作Service
 * @createDate 2026-01-15 18:12:19
 */
public interface QuestionService extends IService<Question> {

    /**
     * 获取题目包装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 获取题目列表查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    LambdaQueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);
}
