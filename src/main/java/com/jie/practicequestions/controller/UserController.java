package com.jie.practicequestions.controller;

import cn.hutool.core.bean.BeanUtil;
import com.jie.practicequestions.common.BaseResponse;
import com.jie.practicequestions.common.ResultUtils;
import com.jie.practicequestions.config.CosClientConfig;
import com.jie.practicequestions.cos.CosManager;
import com.jie.practicequestions.domain.dto.user.EmailRequest;
import com.jie.practicequestions.domain.dto.user.UserChangePwdRequest;
import com.jie.practicequestions.domain.dto.user.UserEditRequest;
import com.jie.practicequestions.domain.model.User;
import com.jie.practicequestions.domain.vo.UserVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private CosClientConfig cosClientConfig;


    /**
     * 头像上传
     */
    @PostMapping("/avatar/upload")
    public BaseResponse<String> uploadAvatar(@RequestPart("file") MultipartFile multipartFile,
                                             HttpServletRequest request) {
        // 文件目录
        User loginUser = userService.getLoginUser(request);
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s", loginUser.getId(), filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            //存入数据库
            loginUser.setAvatar(cosClientConfig.getHost() + File.separator + filepath);
            boolean result = userService.updateById(loginUser);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
            }
            // 返回可访问地址
            return ResultUtils.success(cosClientConfig.getHost() + File.separator + filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 发送邮件
     *
     * @param email
     * @param operation
     * @return
     */
    @GetMapping("/email/send")
    public BaseResponse<Void> sendEmail(String email, String operation) {
        userService.sendEmail(email, operation);
        return ResultUtils.success(null);
    }

    /**
     * 注册
     *
     * @param emailRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody EmailRequest emailRequest) {
        if (BeanUtil.isEmpty(emailRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = userService.register(emailRequest);
        return ResultUtils.success(id);
    }

    /**
     * 登录
     *
     * @param text
     * @param password
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> login(String text, String password, HttpServletRequest request) {
        UserVO userVO = userService.login(text, password, request);
        return ResultUtils.success(userVO);
    }

    /**
     * 登出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        boolean logout = userService.logout(request);
        return ResultUtils.success(logout);
    }

    /**
     * 登录(邮箱验证码)
     *
     * @param emailRequest
     * @param request
     * @return
     */
    @PostMapping("/email/login")
    public BaseResponse<UserVO> loginByEmailCode(@RequestBody EmailRequest emailRequest,
                                                 HttpServletRequest request) {
        if (BeanUtil.isEmpty(emailRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String email = emailRequest.getEmail();
        String emailCode = emailRequest.getEmailCode();
        UserVO userVO = userService.loginByEmailCode(email, emailCode, request);
        return ResultUtils.success(userVO);
    }

    /**
     * 修改密码
     *
     * @param userChangePwdRequest
     * @param request
     * @return
     */
    @PostMapping("/resetPwd")
    public BaseResponse<Boolean> resetPwd(@RequestBody UserChangePwdRequest userChangePwdRequest,
                                          HttpServletRequest request) {
        boolean resetPwd = userService.changeUserPwd(userChangePwdRequest, request);
        return ResultUtils.success(resetPwd);
    }

    /**
     * 修改密码（邮箱验证码）
     */
    @PostMapping("/email/resetPwd")
    public BaseResponse<Boolean> resetPwdByEmailCode(@RequestBody EmailRequest emailRequest,
                                                     HttpServletRequest request) {
        if (BeanUtil.isEmpty(emailRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean resetPwd = userService.changeUserPwdByEmail(emailRequest,request);
        return ResultUtils.success(resetPwd);
    }

    /**
     * 修改邮箱
     */
    @PostMapping("/email/change")
    public BaseResponse<Boolean> changeEmail(@RequestBody EmailRequest emailRequest,
                                             String newEmail,
                                             HttpServletRequest request) {
        if (BeanUtil.isEmpty(emailRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String email = emailRequest.getEmail();
        String emailCode = emailRequest.getEmailCode();
        boolean editEmail = userService.changeUserEmail(email, emailCode, newEmail, request);
        return ResultUtils.success(editEmail);
    }

    /**
     * 修改用户信息
     *
     * @param userEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editUser(@RequestBody UserEditRequest userEditRequest,
                                          HttpServletRequest request) {
        boolean editUser = userService.editUser(userEditRequest, request);
        return ResultUtils.success(editUser);
    }


    /**
     * 获取登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(UserVO.objToVO(loginUser));
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(Long id) {
        User user = userService.getById(id);
        return ResultUtils.success(UserVO.objToVO(user));
    }


}
