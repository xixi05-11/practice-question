package com.jie.practicequestions.controller.user;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.practicequestions.common.BaseResponse;
import com.jie.practicequestions.common.ResultUtils;
import com.jie.practicequestions.domain.dto.questionpractice.QuestionPracticeQueryRequest;
import com.jie.practicequestions.domain.dto.questionpractice.QuestionPracticeSubmitRequest;
import com.jie.practicequestions.domain.model.QuestionPractice;
import com.jie.practicequestions.domain.vo.QuestionPracticeCheckResultVO;
import com.jie.practicequestions.domain.vo.QuestionPracticeVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.service.QuestionPracticeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 练习题接口
 */
@RestController
@RequestMapping("/questionPractice")
@Slf4j
public class QuestionPracticeController {

    @Resource
    private QuestionPracticeService questionPracticeService;

    /**
     * 根据 id 获取练习题
     */
    @GetMapping("/get/id")
    public BaseResponse<QuestionPracticeVO> getQuestionPracticeById(Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "练习题 id 不能为空");
        }
        QuestionPractice questionPractice = questionPracticeService.getById(id);
        QuestionPracticeVO questionPracticeVO = questionPracticeService.getQuestionPracticeVO(questionPractice, request);
        return ResultUtils.success(questionPracticeVO);
    }

    /**
     * 根据问答题 id 获取练习题列表
     */
    @GetMapping("/list/by/question")
    public BaseResponse<List<QuestionPracticeVO>> getQuestionPracticeListByQuestionId(
            @RequestParam Long questionId,
            HttpServletRequest request) {
        if (questionId == null || questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "问答题 id 不能为空");
        }
        LambdaQueryWrapper<QuestionPractice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionPractice::getQuestionId, questionId)
                .orderByAsc(QuestionPractice::getSortOrder);
        List<QuestionPractice> questionPracticeList = questionPracticeService.list(queryWrapper);
        List<QuestionPracticeVO> questionPracticeVOList = questionPracticeList.stream()
                .map(questionPractice -> questionPracticeService.getQuestionPracticeVO(questionPractice, request))
                .toList();
        return ResultUtils.success(questionPracticeVOList);
    }

    /**
     * 分页获取练习题列表
     */
    @PostMapping("/get/list/vo")
    public BaseResponse<Page<QuestionPracticeVO>> getQuestionPracticeList(
            @RequestBody QuestionPracticeQueryRequest questionPracticeQueryRequest,
            HttpServletRequest request) {
        if (BeanUtil.isEmpty(questionPracticeQueryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int pageNum = questionPracticeQueryRequest.getPageNum();
        int pageSize = questionPracticeQueryRequest.getPageSize();
        LambdaQueryWrapper<QuestionPractice> queryWrapper = questionPracticeService.getQueryWrapper(questionPracticeQueryRequest);
        Page<QuestionPractice> page = questionPracticeService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<QuestionPracticeVO> questionPracticeVOList = page.getRecords().stream()
                .map(questionPractice -> questionPracticeService.getQuestionPracticeVO(questionPractice, request))
                .toList();
        Page<QuestionPracticeVO> questionPracticeVOPage = new Page<>(pageNum, pageSize, page.getTotal());
        questionPracticeVOPage.setRecords(questionPracticeVOList);
        return ResultUtils.success(questionPracticeVOPage);
    }

    /**
     * 提交答案并校验
     */
    @PostMapping("/check/answer")
    public BaseResponse<QuestionPracticeCheckResultVO> checkAnswer(@RequestBody QuestionPracticeSubmitRequest submitRequest) {
        if (submitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        QuestionPracticeCheckResultVO resultVO = questionPracticeService.checkAnswer(submitRequest);
        return ResultUtils.success(resultVO);
    }

}