package com.jie.practicequestions.service;

import com.jie.practicequestions.domain.model.Thumb;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 杰
* @description 针对表【thumb(点赞记录表)】的数据库操作Service
* @createDate 2026-01-15 18:12:19
*/
public interface ThumbService extends IService<Thumb> {


    /**
     * 判断用户是否点赞
     */
    Boolean isThumb(Long questionId, HttpServletRequest request);
    /**
     * 点赞
     */
    Boolean doThumb(Long questionId, HttpServletRequest request);

    /**
     * 取消点赞
     */
    Boolean cancelThumb(Long questionId, HttpServletRequest request);


}
