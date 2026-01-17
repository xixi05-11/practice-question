package com.jie.practicequestions.service;

import com.jie.practicequestions.domain.dto.question.EditQuestionStatusRequest;
import com.jie.practicequestions.domain.model.UserQuestionStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 杰
* @description 针对表【user_question_status(用户题目掌握状态表)】的数据库操作Service
* @createDate 2026-01-15 18:12:19
*/
public interface UserQuestionStatusService extends IService<UserQuestionStatus> {

    /**
     * 修改题目掌握状态
     *
     * @param editQuestionStatusRequest
     * @param request
     */
    void editQuestionStatus(EditQuestionStatusRequest editQuestionStatusRequest, HttpServletRequest request);
}
