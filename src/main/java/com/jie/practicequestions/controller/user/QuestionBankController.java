package com.jie.practicequestions.controller.user;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.practicequestions.common.BaseResponse;
import com.jie.practicequestions.common.ResultUtils;
import com.jie.practicequestions.domain.dto.question.QuestionQueryRequest;
import com.jie.practicequestions.domain.dto.questionbank.QuestionBankQueryRequest;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.model.QuestionBank;
import com.jie.practicequestions.domain.model.User;
import com.jie.practicequestions.domain.vo.QuestionBankVO;
import com.jie.practicequestions.domain.vo.QuestionVO;
import com.jie.practicequestions.domain.vo.UserVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.service.QuestionBankService;
import com.jie.practicequestions.service.QuestionService;
import com.jie.practicequestions.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/questionBank")
public class QuestionBankController {

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * 获取题库
     */
    @GetMapping("/get/id")
    public BaseResponse<QuestionBankVO> getQuestionBankById(Long id, HttpServletRequest request) {
        QuestionBank questionBank = questionBankService.getById(id);
        Long userId = questionBank.getUserId();
        User user = userService.getById(userId);
        // 设置题目的查询条件
        QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
        questionQueryRequest.setQuestionBankId(id);
        LambdaQueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);
        // 获取题目列表
        List<Question> questionList = questionService.list(queryWrapper);
        // 转换成 VO
        List<QuestionVO> questionVOList = questionList.stream().map(
                question -> questionService.getQuestionVO(question, request))
                .toList();
        QuestionBankVO questionBankVO = QuestionBankVO.objToVO(questionBank, UserVO.objToVO(user));
        questionBankVO.setQuestionVOList(questionVOList);
        return ResultUtils.success(questionBankVO);
    }

    /**
     * 获取题库列表
     */
    @PostMapping("/get/list")
    public BaseResponse<Page<QuestionBankVO>> getQuestionBankList(@RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
        if (BeanUtil.isEmpty(questionBankQueryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int pageNum = questionBankQueryRequest.getPageNum();
        int pageSize = questionBankQueryRequest.getPageSize();
        LambdaQueryWrapper<QuestionBank> wrapper = questionBankService.getQueryWrapper(questionBankQueryRequest);
        Page<QuestionBank> page = questionBankService.page(new Page<>(pageNum, pageSize), wrapper);
        // 转换成 VO
        List<QuestionBankVO> questionBankVOList = page.getRecords().stream().map(questionBank ->
                        QuestionBankVO.objToVO(questionBank, UserVO.objToVO(userService.getById(questionBank.getUserId()))))
                .toList();
        Page<QuestionBankVO> questionBankVOPage = new Page<>(pageNum, pageSize, page.getTotal());
        questionBankVOPage.setRecords(questionBankVOList);
        return ResultUtils.success(questionBankVOPage);
    }

}
