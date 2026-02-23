package com.jie.practicequestions.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jie.practicequestions.domain.dto.questioncomment.QuestionCommentAddRequest;
import com.jie.practicequestions.domain.dto.questioncomment.QuestionCommentQueryRequest;
import com.jie.practicequestions.domain.model.QuestionComment;
import com.jie.practicequestions.domain.vo.QuestionCommentVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 杰
* @description 针对表【question_comment(题目评论表)】的数据库操作Service
* @createDate 2026-02-21 20:27:16
*/
public interface QuestionCommentService extends IService<QuestionComment> {

    /**
     * 发表评论
     *
     * @param questionCommentAddRequest
     * @param request
     * @return 评论 id
     */
    Long addComment(QuestionCommentAddRequest questionCommentAddRequest, HttpServletRequest request);

    /**
     * 获取评论 VO
     *
     * @param questionComment
     * @param request
     * @return 评论 VO
     */
    QuestionCommentVO getQuestionCommentVO(QuestionComment questionComment, HttpServletRequest request);

    /**
     * 获取评论列表查询条件
     *
     * @param questionCommentQueryRequest
     * @return
     */
    LambdaQueryWrapper<QuestionComment> getQueryWrapper(QuestionCommentQueryRequest questionCommentQueryRequest);

    /**
     * 获取评论列表（包含子评论）
     *
     * @param questionCommentQueryRequest 评论查询请求
     * @param request                    请求
     * @return 分页评论列表
     */
    Page<QuestionCommentVO> getCommentListWithChildren(QuestionCommentQueryRequest questionCommentQueryRequest, HttpServletRequest request);
}
