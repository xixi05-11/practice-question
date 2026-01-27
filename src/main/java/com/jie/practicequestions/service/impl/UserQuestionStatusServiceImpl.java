package com.jie.practicequestions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.practicequestions.domain.dto.question.EditQuestionStatusRequest;
import com.jie.practicequestions.domain.enums.UserQuestionStatusEnum;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.model.UserQuestionStatus;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.service.QuestionService;
import com.jie.practicequestions.service.UserQuestionStatusService;
import com.jie.practicequestions.mapper.UserQuestionStatusMapper;
import com.jie.practicequestions.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
* @author 杰
* @description 针对表【user_question_status(用户题目掌握状态表)】的数据库操作Service实现
* @createDate 2026-01-15 18:12:19
*/
@Service
public class UserQuestionStatusServiceImpl extends ServiceImpl<UserQuestionStatusMapper, UserQuestionStatus>
    implements UserQuestionStatusService{

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private QuestionService questionService;

    @Override
    public void editQuestionStatus(EditQuestionStatusRequest editQuestionStatusRequest,
                                   HttpServletRequest request) {
        Long userId = userService.getLoginUser(request).getId();

        Long questionId = editQuestionStatusRequest.getQuestionId();
        Integer status = editQuestionStatusRequest.getStatus();
        //判断 questionId 是否存在
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }
        // 判断 status 是否存在
        UserQuestionStatusEnum userQuestionStatusEnum = UserQuestionStatusEnum.getEnumByValue(status);
        if (userQuestionStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<UserQuestionStatus> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQuestionStatus::getUserId, userId);
        queryWrapper.eq(UserQuestionStatus::getQuestionId, questionId);
        UserQuestionStatus userQuestionStatus = this.getOne(queryWrapper);
        if(userQuestionStatus != null){
            if(userQuestionStatus.getStatus().equals(userQuestionStatusEnum.getValue())){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "请勿重复操作");
            }
            userQuestionStatus.setUpdateTime(LocalDateTime.now());
            userQuestionStatus.setStatus(userQuestionStatusEnum.getValue());
            boolean update = this.update(userQuestionStatus, queryWrapper);
            if (!update) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败");
            }
        }else {
            userQuestionStatus = new UserQuestionStatus();
            userQuestionStatus.setUserId(userId);
            userQuestionStatus.setQuestionId(questionId);
            userQuestionStatus.setStatus(userQuestionStatusEnum.getValue());
            userQuestionStatus.setUpdateTime(LocalDateTime.now());
            boolean update = this.save(userQuestionStatus);
            if (!update) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败");
            }
        }
    }
}




