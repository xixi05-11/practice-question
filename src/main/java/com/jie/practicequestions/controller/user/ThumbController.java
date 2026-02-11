package com.jie.practicequestions.controller.user;

import com.jie.practicequestions.common.BaseResponse;
import com.jie.practicequestions.common.ResultUtils;
import com.jie.practicequestions.service.ThumbService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/thumb")
@Slf4j
public class ThumbController {

    @Resource
    private ThumbService thumbService;

    /**
     * 判断用户是否点赞
     *
     * @param questionId
     * @param request
     * @return
     */
    @GetMapping("/isThumb")
    public BaseResponse<Boolean> isThumb(Long questionId, HttpServletRequest request) {
        return ResultUtils.success(thumbService.isThumb(questionId, request));
    }

    /**
     * 点赞
     *
     * @param questionId
     * @param request
     * @return
     */
    @GetMapping("/doThumb")
    public BaseResponse<Boolean> doThumb(Long questionId, HttpServletRequest request) {
        return ResultUtils.success(thumbService.doThumb(questionId, request));
    }

    /**
     * 取消点赞
     *
     * @param questionId
     * @param request
     * @return
     */
    @GetMapping("/cancelThumb")
    public BaseResponse<Boolean> cancelThumb(Long questionId, HttpServletRequest request) {
        return ResultUtils.success(thumbService.cancelThumb(questionId, request));
    }
}
