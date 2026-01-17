package com.jie.practicequestions.domain.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证码请求
 */
@Data
public class EmailRequest implements Serializable {

    /***
     * 邮箱
     */
    private String email;
    /**
     * 验证码
     */
    private String emailCode;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String checkPassword;

    @Serial
    private static final long serialVersionUID = 1L;
}
