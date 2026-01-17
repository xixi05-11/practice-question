package com.jie.practicequestions.domain.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户修改密码
 */
@Data
public class UserChangePwdRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户 id
     */
    private Long id;
    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
