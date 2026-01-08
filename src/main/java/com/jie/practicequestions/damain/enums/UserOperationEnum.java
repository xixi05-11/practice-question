package com.jie.practicequestions.damain.enums;

import cn.hutool.core.util.ObjUtil;
import com.jie.practicequestions.constant.UserOperationConstant;
import lombok.Getter;

@Getter
public enum UserOperationEnum {

    LOGIN("登录", UserOperationConstant.LOGIN, true),
    REGISTER("注册", UserOperationConstant.REGISTER, false),
    RESET_PWD("重置密码", UserOperationConstant.RESET_PWD, true),
    CHANGE_EMAIL("修改邮箱", UserOperationConstant.CHANGE_EMAIL, true);
    private final String text;
    private final String value;
    private final boolean isExist;

    UserOperationEnum(String text, String value, boolean isExist) {
        this.text = text;
        this.value = value;
        this.isExist = isExist;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的 value
     * @return 枚举值
     */
    public static UserOperationEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserOperationEnum anEnum : UserOperationEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取redis key
     */
    public String getRedisKey(String email) {
        return value + "_" + "email_" + email;
    }
}
