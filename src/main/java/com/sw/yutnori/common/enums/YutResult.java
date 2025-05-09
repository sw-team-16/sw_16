package com.sw.yutnori.common.enums;

public enum YutResult {
    BACK_DO("빽도"),
    DO("도"),
    GAE("개"),
    GEOL("걸"),
    YUT("윷"),
    MO("모");

    private final String displayName;

    YutResult(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

