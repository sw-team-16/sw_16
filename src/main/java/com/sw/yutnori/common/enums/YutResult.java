package com.sw.yutnori.common.enums;

public enum YutResult {
    BACK_DO("빽도", -1),
    DO("도", 1),
    GAE("개", 2),
    GEOL("걸", 3),
    YUT("윷", 4),
    MO("모", 5);

    private final String displayName;
    private final int stepCount;

    YutResult(String displayName, int stepCount) {
        this.displayName = displayName;
        this.stepCount = stepCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getStepCount() {
        return stepCount;
    }
}
