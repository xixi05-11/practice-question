package com.jie.practicequestions.controller.user;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.practicequestions.common.BaseResponse;
import com.jie.practicequestions.common.ResultUtils;
import com.jie.practicequestions.domain.dto.question.EditQuestionStatusRequest;
import com.jie.practicequestions.domain.dto.question.QuestionQueryRequest;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.vo.QuestionVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.service.QuestionService;
import com.jie.practicequestions.service.UserQuestionStatusService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserQuestionStatusService userQuestionStatusService;

    /**
     * 获取题目
     */
    @GetMapping("/get/id")
    public BaseResponse<QuestionVO> getQuestionVOById(Long id, HttpServletRequest request) {
        Question question = questionService.getById(id);
        QuestionVO questionVO = questionService.getQuestionVO(question, request);
        return ResultUtils.success(questionVO);
    }

    /**
     * 获取题目列表
     */
    @PostMapping("/get/list/vo")
    public BaseResponse<Page<QuestionVO>> getQuestionList(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                          HttpServletRequest request) {
        if(questionQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int pageNum = questionQueryRequest.getPageNum();
        int pageSize = questionQueryRequest.getPageSize();
        LambdaQueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);
        Page<Question> page = questionService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<QuestionVO> questionVOList = page.getRecords().stream().map(question ->
                questionService.getQuestionVO(question, request)).toList();
        Page<QuestionVO> questionVOPage = new Page<>(pageNum, pageSize, page.getTotal());
        questionVOPage.setRecords(questionVOList);
        return ResultUtils.success(questionVOPage);
    }

    /**
     * 修改题目状态
     */
    @PostMapping("/update/status")
    public BaseResponse<Boolean> editQuestionStatus(@RequestBody EditQuestionStatusRequest editQuestionStatusRequest,
                                                    HttpServletRequest request) {
        if(BeanUtil.isEmpty(editQuestionStatusRequest)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userQuestionStatusService.editQuestionStatus(editQuestionStatusRequest, request);
        return ResultUtils.success(true);
    }

}
