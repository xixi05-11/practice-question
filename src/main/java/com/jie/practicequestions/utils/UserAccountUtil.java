package com.jie.practicequestions.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class UserAccountUtil {

    public static String getUserAccount() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String id = snowflake.nextIdStr();
        return id.substring(0, 13);
    }
}
