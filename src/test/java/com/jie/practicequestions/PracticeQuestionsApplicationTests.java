package com.jie.practicequestions;

import cn.hutool.core.lang.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


class PracticeQuestionsApplicationTests {
    public static void main(String[] args) {
        System.out.println(Validator.isEmail("asdada@qq.co"));
    }
}
