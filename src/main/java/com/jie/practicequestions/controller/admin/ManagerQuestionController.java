package com.jie.practicequestions.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.practicequestions.annotation.AuthCheck;
import com.jie.practicequestions.common.BaseResponse;
import com.jie.practicequestions.common.DeleteRequest;
import com.jie.practicequestions.common.ResultUtils;
import com.jie.practicequestions.constant.UserConstant;
import com.jie.practicequestions.domain.dto.question.QuestionAddRequest;
import com.jie.practicequestions.domain.dto.question.QuestionEditRequest;
import com.jie.practicequestions.domain.dto.question.QuestionQueryRequest;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.model.QuestionBankQuestion;
import com.jie.practicequestions.domain.model.User;
import com.jie.practicequestions.domain.vo.QuestionVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.exception.ThrowUtils;
import com.jie.practicequestions.service.QuestionBankQuestionService;
import com.jie.practicequestions.service.QuestionService;
import com.jie.practicequestions.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目管理接口
 */
@RestController
@RequestMapping("/admin/question")
@Slf4j
public class ManagerQuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    /**
     * 创建题目
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = questionAddRequest.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目标题不能为空");
        }
        Question question = new Question();
        BeanUtil.copyProperties(questionAddRequest, question);
        // 将标签列表转换为 JSON 字符串
        List<String> tagList = questionAddRequest.getTagList();
        if (CollUtil.isNotEmpty(tagList)) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "创建题目失败");
        // 关联题库
        List<Long> questionBankIdList = questionAddRequest.getQuestionBankIdList();
        if (CollUtil.isNotEmpty(questionBankIdList)) {
            for (Long questionBankId : questionBankIdList) {
                QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                questionBankQuestion.setQuestionId(question.getId());
                questionBankQuestion.setQuestionBankId(questionBankId);
                questionBankQuestionService.save(questionBankQuestion);
            }
        }
        return ResultUtils.success(question.getId());
    }

    /**
     * 删除题目
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 检查题目是否存在
        Question question = questionService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        // 删除题目与题库的关联关系
        LambdaQueryWrapper<QuestionBankQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionBankQuestion::getQuestionId, deleteRequest.getId());
        questionBankQuestionService.remove(wrapper);
        // 删除题目
        boolean result = questionService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除题目失败");
        return ResultUtils.success(true);
    }

    /**
     * 更新题目
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionEditRequest questionEditRequest,
                                                HttpServletRequest  request) {
        if (questionEditRequest == null || questionEditRequest.getId() == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 检查题目是否存在
        Question existingQuestion = questionService.getById(questionEditRequest.getId());
        ThrowUtils.throwIf(existingQuestion == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        Question question = new Question();
        BeanUtil.copyProperties(questionEditRequest, question);
        // 将标签列表转换为 JSON 字符串
        List<String> tagList = questionEditRequest.getTagList();
        if (CollUtil.isNotEmpty(tagList)) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新题目失败");
        // 更新题库关联关系
        List<Long> questionBankIdList = questionEditRequest.getQuestionBankIdList();
        if (questionBankIdList != null) {
            // 先删除原有的关联关系
            LambdaQueryWrapper<QuestionBankQuestion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(QuestionBankQuestion::getQuestionId, questionEditRequest.getId());
            questionBankQuestionService.remove(wrapper);
            // 添加新的关联关系
            if (CollUtil.isNotEmpty(questionBankIdList)) {
                for (Long questionBankId : questionBankIdList) {
                    QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                    questionBankQuestion.setQuestionId(questionEditRequest.getId());
                    questionBankQuestion.setQuestionBankId(questionBankId);
                    questionBankQuestion.setUserId(loginUser.getId());
                    questionBankQuestionService.save(questionBankQuestion);
                }
            }
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<QuestionVO> getQuestionById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        QuestionVO questionVO = questionService.getQuestionVO(question, request);
        return ResultUtils.success(questionVO);
    }

    /**
     * 分页获取题目列表
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = questionQueryRequest.getPageNum();
        long pageSize = questionQueryRequest.getPageSize();
        LambdaQueryWrapper<Question> wrapper = questionService.getQueryWrapper(questionQueryRequest);
        Page<Question> questionPage = questionService.page(Page.of(pageNum, pageSize), wrapper);
        // 转换为 VO
        Page<QuestionVO> questionVOPage = new Page<>(pageNum, pageSize, questionPage.getTotal());
        List<Question> questionList = questionPage.getRecords();
        List<QuestionVO> questionVOList = questionList.stream()
                .map(question -> questionService.getQuestionVO(question, request))
                .toList();
        questionVOPage.setRecords(questionVOList);
        return ResultUtils.success(questionVOPage);
    }
}