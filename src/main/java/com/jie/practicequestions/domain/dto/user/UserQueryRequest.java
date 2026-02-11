package com.jie.practicequestions.domain.dto.user;



import com.jie.practicequestions.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 创建时间开始
     */
    private LocalDateTime startCreateTime;

    /**
     * 创建时间结束
     */
    private LocalDateTime endCreateTime;

    /**
     * 编辑时间开始
     */
    private LocalDateTime startEditTime;

    /**
     * 编辑时间结束
     */
    private LocalDateTime endEditTime;

    @Serial
    private static final long serialVersionUID = 1L;
}
