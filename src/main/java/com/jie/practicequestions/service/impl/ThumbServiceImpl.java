package com.jie.practicequestions.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.model.Thumb;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.mapper.ThumbMapper;
import com.jie.practicequestions.service.QuestionService;
import com.jie.practicequestions.service.ThumbService;
import com.jie.practicequestions.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author 杰
 * @description 针对表【thumb(点赞记录表)】的数据库操作Service实现
 * @createDate 2026-01-15 18:12:19
 */
@Service
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb>
        implements ThumbService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private QuestionService questionService;

    @Resource
    private TransactionTemplate transactionTemplate;


    @Override
    public Boolean isThumb(Long questionId, HttpServletRequest request) {
        Long userId = userService.getLoginUser(request).getId();
        Thumb thumb = this.lambdaQuery().eq(Thumb::getQuestionId, questionId)
                .eq(Thumb::getUserId, userId)
                .one();
        return BeanUtil.isNotEmpty(thumb);
    }

    /**
     * 点赞
     *
     * @param questionId
     * @param request
     * @return
     */
    @Override
    public Boolean doThumb(Long questionId, HttpServletRequest request) {
        long userId = userService.getLoginUser(request).getId();
        if (this.isThumb(questionId, request)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请勿重复点赞");
        }
        // 事务操作
        return transactionTemplate.execute(status -> {
            Thumb thumb = new Thumb();
            thumb.setQuestionId(questionId);
            thumb.setUserId(userId);
            boolean save = this.save(thumb);
            if (!save) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "点赞失败");
            }
            boolean update = questionService.lambdaUpdate().eq(Question::getId, questionId)
                    .setSql("thumb_num = thumb_num - 1")
                    .update();
            if (!update) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "点赞失败");
            }
            return true;
        });
    }

    @Override
    public Boolean cancelThumb(Long questionId, HttpServletRequest request) {
        long userId = userService.getLoginUser(request).getId();
        if (!this.isThumb(questionId, request)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请勿重复取消点赞");
        }
        return transactionTemplate.execute(status -> {
            Thumb thumb = this.lambdaQuery().eq(Thumb::getQuestionId, questionId)
                    .eq(Thumb::getUserId, userId)
                    .one();
            boolean delete = this.removeById(thumb);
            if (!delete) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "取消点赞失败");
            }
            boolean update = questionService.lambdaUpdate().eq(Question::getId, questionId)
                    .setSql("thumb_num = thumb_num - 1")
                    .update();
            if (!update) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "取消点赞失败");
            }
            return true;
        });
    }
}




