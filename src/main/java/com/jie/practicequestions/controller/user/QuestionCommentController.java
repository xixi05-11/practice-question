package com.jie.practicequestions.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.practicequestions.common.BaseResponse;
import com.jie.practicequestions.common.ResultUtils;
import com.jie.practicequestions.domain.dto.questioncomment.QuestionCommentAddRequest;
import com.jie.practicequestions.domain.dto.questioncomment.QuestionCommentQueryRequest;
import com.jie.practicequestions.domain.model.QuestionComment;
import com.jie.practicequestions.domain.vo.QuestionCommentVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.service.QuestionCommentService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目评论接口
 */
@RestController
@RequestMapping("/question/comment")
@Slf4j
public class QuestionCommentController {

    @Resource
    private QuestionCommentService questionCommentService;

    /**
     * 发表评论
     *
     * @param questionCommentAddRequest 评论添加请求
     * @param request                  请求
     * @return 评论 id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addComment(@RequestBody QuestionCommentAddRequest questionCommentAddRequest,
                                         HttpServletRequest request) {
        if (questionCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long commentId = questionCommentService.addComment(questionCommentAddRequest, request);
        return ResultUtils.success(commentId);
    }

    /**
     * 获取评论详情
     *
     * @param id      评论 id
     * @param request 请求
     * @return 评论详情
     */
    @PostMapping("/get")
    public BaseResponse<QuestionCommentVO> getComment(Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionComment questionComment = questionCommentService.getById(id);
        QuestionCommentVO questionCommentVO = questionCommentService.getQuestionCommentVO(questionComment, request);
        return ResultUtils.success(questionCommentVO);
    }

    /**
     * 获取评论列表（包含子评论）
     *
     * @param questionCommentQueryRequest 评论查询请求
     * @param request                    请求
     * @return 评论列表
     */
    @PostMapping("/get/list/vo")
    public BaseResponse<Page<QuestionCommentVO>> getCommentList(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionCommentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<QuestionCommentVO> commentVOPage = questionCommentService.getCommentListWithChildren(questionCommentQueryRequest, request);
        return ResultUtils.success(commentVOPage);
    }

    /**
     * 获取某条根评论的所有子评论
     *
     * @param questionCommentQueryRequest 评论查询请求（需提供 rootId）
     * @param request                    请求
     * @return 子评论列表
     */
    @PostMapping("/get/children/list")
    public BaseResponse<Page<QuestionCommentVO>> getChildrenList(@RequestBody QuestionCommentQueryRequest questionCommentQueryRequest,
                                                                  HttpServletRequest request) {
        if (questionCommentQueryRequest == null || questionCommentQueryRequest.getRootId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "rootId 不能为空");
        }
        int pageNum = questionCommentQueryRequest.getPageNum();
        int pageSize = questionCommentQueryRequest.getPageSize();
        LambdaQueryWrapper<QuestionComment> queryWrapper = questionCommentService.getQueryWrapper(questionCommentQueryRequest);
        Page<QuestionComment> page = questionCommentService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<QuestionCommentVO> questionCommentVOList = page.getRecords().stream()
                .map(comment -> questionCommentService.getQuestionCommentVO(comment, request))
                .toList();
        Page<QuestionCommentVO> questionCommentVOPage = new Page<>(pageNum, pageSize, page.getTotal());
        questionCommentVOPage.setRecords(questionCommentVOList);
        return ResultUtils.success(questionCommentVOPage);
    }
}