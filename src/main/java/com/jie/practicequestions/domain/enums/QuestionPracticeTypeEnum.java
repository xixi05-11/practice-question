package com.jie.practicequestions.domain.enums;

import lombok.Getter;

/**
 * 练习题题型枚举
 */
@Getter
public enum QuestionPracticeTypeEnum {

    SINGLE_CHOICE("单选", 0),
    MULTIPLE_CHOICE("多选", 1),
    TRUE_FALSE("判断", 2);

    private final String text;
    private final Integer value;

    QuestionPracticeTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static QuestionPracticeTypeEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (QuestionPracticeTypeEnum valueEnum : QuestionPracticeTypeEnum.values()) {
            if (valueEnum.value.equals(value)) {
                return valueEnum;
            }
        }
        return null;
    }

    public static QuestionPracticeTypeEnum getEnumByText(String text) {
        if (text == null) {
            return null;
        }
        for (QuestionPracticeTypeEnum valueEnum : QuestionPracticeTypeEnum.values()) {
            if (valueEnum.text.equals(text)) {
                return valueEnum;
            }
        }
        return null;
    }
}