package com.sw.yutnori.common;

import lombok.Getter;

@Getter
public class LogicalPosition {
    private Long pieceId;
    private int a;
    private int b;

    public LogicalPosition(Long pieceId, int a, int b) {
        this.pieceId = pieceId;
        this.a = a;
        this.b = b;
    }

    public LogicalPosition(int a, int b) {
        this(null, a, b);
    }

}
