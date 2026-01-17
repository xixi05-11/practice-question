package com.jie.practicequestions.domain.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserEditRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 简介
     */
    private String profile;

    /**
     * 头像
     */
    private String avatar;

    @Serial
    private static final long serialVersionUID = 1L;
}
