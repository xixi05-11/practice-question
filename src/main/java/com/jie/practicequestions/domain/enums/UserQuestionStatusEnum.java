package com.jie.practicequestions.domain.enums;

import lombok.Getter;

@Getter
public enum UserQuestionStatusEnum {

    NOT_DONE("未做", 0),
    MASTERED("已掌握", 1),
    LATER("稍后再刷", 2),
    ERROR_PRONE("易错", 3);
    private final String text;
    private final Integer value;

    UserQuestionStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static UserQuestionStatusEnum getEnumByValue(Integer value) {
        for (UserQuestionStatusEnum valueEnum : UserQuestionStatusEnum.values()) {
            if (valueEnum.value.equals(value)) {
                return valueEnum;
            }
        }
        return null;
    }

    public static UserQuestionStatusEnum getEnumByText(String text) {
        for (UserQuestionStatusEnum valueEnum : UserQuestionStatusEnum.values()) {
            if (valueEnum.text.equals(text)) {
                return valueEnum;
            }
        }
        return null;
    }
}
