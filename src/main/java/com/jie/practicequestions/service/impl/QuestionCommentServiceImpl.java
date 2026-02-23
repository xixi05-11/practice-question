package com.jie.practicequestions.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.practicequestions.domain.dto.questioncomment.QuestionCommentAddRequest;
import com.jie.practicequestions.domain.dto.questioncomment.QuestionCommentQueryRequest;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.model.QuestionComment;
import com.jie.practicequestions.domain.model.User;
import com.jie.practicequestions.domain.vo.QuestionCommentVO;
import com.jie.practicequestions.domain.vo.UserVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.mapper.QuestionCommentMapper;
import com.jie.practicequestions.service.QuestionCommentService;
import com.jie.practicequestions.service.QuestionService;
import com.jie.practicequestions.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 杰
* @description 针对表【question_comment(题目评论表)】的数据库操作Service实现
* @createDate 2026-02-21 20:27:16
*/
@Service
public class QuestionCommentServiceImpl extends ServiceImpl<QuestionCommentMapper, QuestionComment>
    implements QuestionCommentService{

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    /**
     * 发表评论
     *
     * @param questionCommentAddRequest
     * @param request
     * @return
     */
    @Override
    public Long addComment(QuestionCommentAddRequest questionCommentAddRequest, HttpServletRequest request) {
        if (questionCommentAddRequest == null || StrUtil.isBlank(questionCommentAddRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }

        Long questionId = questionCommentAddRequest.getQuestionId();
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目 id 不能为空");
        }

        // 校验题目是否存在
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();

        // 构建评论对象
        QuestionComment questionComment = new QuestionComment();
        questionComment.setQuestionId(questionId);
        questionComment.setUserId(userId);
        questionComment.setContent(questionCommentAddRequest.getContent());
        questionComment.setThumbNum(0);

        // 处理回复评论的逻辑
        Long parentId = questionCommentAddRequest.getParentId();
        if (parentId != null) {
            // 回复评论
            QuestionComment parentComment = this.getById(parentId);
            if (parentComment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "父评论不存在");
            }

            // 验证父评论是否属于同一个题目
            if (!parentComment.getQuestionId().equals(questionId)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "父评论不属于该题目");
            }

            questionComment.setParentId(parentId);

            // 设置 rootId
            if (parentComment.getRootId() == null) {
                // 父评论是一级评论，rootId 指向父评论
                questionComment.setRootId(parentId);
            } else {
                // 父评论是二级评论，rootId 继承父评论的 rootId
                questionComment.setRootId(parentComment.getRootId());
            }

            // 设置被回复用户
            Long replyUserId = questionCommentAddRequest.getReplyUserId();
            if (replyUserId == null) {
                questionComment.setReplyUserId(parentComment.getUserId());
            } else {
                questionComment.setReplyUserId(replyUserId);
            }
        } else {
            // 一级评论，parent_id 和 root_id 都为 null
            questionComment.setParentId(null);
            questionComment.setRootId(null);
        }

        // 保存评论
        boolean save = this.save(questionComment);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发表评论失败");
        }

        return questionComment.getId();
    }

    /**
     * 获取评论 VO
     *
     * @param questionComment
     * @param request
     * @return
     */
    @Override
    public QuestionCommentVO getQuestionCommentVO(QuestionComment questionComment, HttpServletRequest request) {
        if (questionComment == null) {
            return null;
        }

        QuestionCommentVO questionCommentVO = new QuestionCommentVO();
        BeanUtil.copyProperties(questionComment, questionCommentVO);

        // 填充评论用户信息
        Long userId = questionComment.getUserId();
        User user = userService.getById(userId);
        if (user != null) {
            UserVO userVO = UserVO.objToVO(user);
            questionCommentVO.setUserVO(userVO);
        }

        // 填充被回复用户信息
        Long replyUserId = questionComment.getReplyUserId();
        if (replyUserId != null) {
            User replyUser = userService.getById(replyUserId);
            if (replyUser != null) {
                UserVO replyUserVO = UserVO.objToVO(replyUser);
                questionCommentVO.setReplyUserVO(replyUserVO);
            }
        }

        return questionCommentVO;
    }

    /**
     * 获取评论列表查询条件
     *
     * @param questionCommentQueryRequest
     * @return
     */
    @Override
    public LambdaQueryWrapper<QuestionComment> getQueryWrapper(QuestionCommentQueryRequest questionCommentQueryRequest) {
        LambdaQueryWrapper<QuestionComment> queryWrapper = new LambdaQueryWrapper<>();

        Long questionId = questionCommentQueryRequest.getQuestionId();
        Long rootId = questionCommentQueryRequest.getRootId();
        Long userId = questionCommentQueryRequest.getUserId();

        // 按题目 id 查询
        if (questionId != null) {
            queryWrapper.eq(QuestionComment::getQuestionId, questionId);
        }

        // 按 rootId 查询（如果提供了 rootId，说明是查询某条一级评论下的所有回复）
        if (rootId != null) {
            queryWrapper.eq(QuestionComment::getRootId, rootId);
        } else {
            // 如果没有提供 rootId，默认查询一级评论（parent_id 为 null）
            queryWrapper.isNull(QuestionComment::getParentId);
        }

        // 按用户 id 查询
        if (userId != null) {
            queryWrapper.eq(QuestionComment::getUserId, userId);
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(QuestionComment::getCreateTime);

        return queryWrapper;
    }

    /**
     * 获取评论列表（包含子评论）
     *
     * @param questionCommentQueryRequest 评论查询请求
     * @param request                    请求
     * @return 分页评论列表
     */
    @Override
    public Page<QuestionCommentVO> getCommentListWithChildren(QuestionCommentQueryRequest questionCommentQueryRequest,
                                                              HttpServletRequest request) {
        int pageNum = questionCommentQueryRequest.getPageNum();
        int pageSize = questionCommentQueryRequest.getPageSize();
        Integer childrenLimit = questionCommentQueryRequest.getChildrenLimit();

        // 1. 查询一级评论（分页）
        LambdaQueryWrapper<QuestionComment> rootWrapper = new LambdaQueryWrapper<>();
        Long questionId = questionCommentQueryRequest.getQuestionId();
        if (questionId != null) {
            rootWrapper.eq(QuestionComment::getQuestionId, questionId);
        }
        rootWrapper.isNull(QuestionComment::getParentId);
        rootWrapper.orderByDesc(QuestionComment::getCreateTime);

        Page<QuestionComment> rootPage = this.page(new Page<>(pageNum, pageSize), rootWrapper);

        // 2. 转换为 VO 并填充子评论
        List<QuestionCommentVO> rootVOList = rootPage.getRecords().stream()
                .map(rootComment -> {
                    QuestionCommentVO rootVO = getQuestionCommentVO(rootComment, request);

                    // 查询该根评论的子评论
                    LambdaQueryWrapper<QuestionComment> childWrapper = new LambdaQueryWrapper<>();
                    childWrapper.eq(QuestionComment::getRootId, rootComment.getId());
                    childWrapper.orderByAsc(QuestionComment::getCreateTime);

                    // 先统计子评论总数
                    long childrenCount = this.count(childWrapper);
                    rootVO.setChildrenCount(childrenCount);

                    // 限制子评论数量
                    if (childrenLimit != null && childrenLimit > 0) {
                        childWrapper.last("LIMIT " + childrenLimit);
                    }

                    List<QuestionComment> children = this.list(childWrapper);
                    List<QuestionCommentVO> childVOList = children.stream()
                            .map(child -> getQuestionCommentVO(child, request))
                            .toList();

                    rootVO.setChildren(childVOList);
                    return rootVO;
                })
                .toList();

        // 3. 组装返回结果
        Page<QuestionCommentVO> resultPage = new Page<>(pageNum, pageSize, rootPage.getTotal());
        resultPage.setRecords(rootVOList);
        return resultPage;
    }
}




