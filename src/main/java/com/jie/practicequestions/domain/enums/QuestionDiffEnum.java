package com.jie.practicequestions.domain.enums;

import lombok.Getter;

@Getter
public enum QuestionDiffEnum {
    EASY("简单", 0),
    MEDIUM("中等", 1),
    HARD("困难", 2);
    private final String text;
    private final Integer value;

    QuestionDiffEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static QuestionDiffEnum getEnumByValue(Integer value) {
        for (QuestionDiffEnum valueEnum : QuestionDiffEnum.values()) {
            if (valueEnum.value.equals(value)) {
                return valueEnum;
            }
        }
        return null;
    }

    public static QuestionDiffEnum getEnumByText(String text) {
        for (QuestionDiffEnum valueEnum : QuestionDiffEnum.values()) {
            if (valueEnum.text.equals(text)) {
                return valueEnum;
            }
        }
        return null;
    }
}
