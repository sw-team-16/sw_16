// model/enums/YutResult.java
package com.sw.yutnori.model.enums;

public enum YutResult {
    BACK_DO(-1),
    DO(1),
    GAE(2),
    GEOL(3),
    YUT(4),
    MO(5);

    private final int stepCount;

    YutResult(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getStepCount() {
        return stepCount;
    }
}
