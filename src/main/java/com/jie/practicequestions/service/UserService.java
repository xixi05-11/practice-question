package com.jie.practicequestions.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jie.practicequestions.domain.dto.EmailRequest;
import com.jie.practicequestions.domain.dto.UserChangePwdRequest;
import com.jie.practicequestions.domain.dto.UserEditRequest;
import com.jie.practicequestions.domain.model.User;
import com.jie.practicequestions.domain.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author 杰
 * @description 针对表【s_user(用户)】的数据库操作Service
 * @createDate 2026-01-08 14:32:10
 */
public interface UserService extends IService<User> {


    /**
     * 发送邮件
     */
    void sendEmail(String email, String operation);

    /**
     * 用户注册
     *
     * @param emailRequest
     * @return
     */
    Long register(EmailRequest emailRequest);

    /**
     * 用户登录
     *
     * @param text     用户账户/邮箱
     * @param password 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    UserVO login(String text, String password, HttpServletRequest request);


    /**
     * 用户邮箱验证码
     *
     * @param email
     * @param emailCode
     * @param request
     * @return 脱敏后的用户信息
     */
    UserVO loginByEmailCode(String email, String emailCode, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean logout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 修改密码
     *
     * @param userChangePwdRequest
     * @return
     */
    boolean changeUserPwd(UserChangePwdRequest userChangePwdRequest, HttpServletRequest request);


    /**
     * 修改密码（邮箱验证码）
     *
     * @param emailRequest
     * @param request
     * @return
     */
    boolean changeUserPwdByEmail(EmailRequest emailRequest, HttpServletRequest request);

    /**
     * 修改邮箱
     */
    boolean changeUserEmail(String email, String emailCode, String newEmail, HttpServletRequest request);

    /**
     * 修改用户信息
     *
     * @param userEditRequest
     * @param request
     * @return
     */
    boolean editUser(UserEditRequest userEditRequest, HttpServletRequest request);
}
