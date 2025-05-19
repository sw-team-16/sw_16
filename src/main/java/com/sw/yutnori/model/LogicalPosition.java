package com.sw.yutnori.model;

import java.util.Objects;

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

    public Long getPieceId() { return pieceId; }
    public int getA() { return a; }
    public int getB() { return b; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicalPosition that = (LogicalPosition) o;
        return a == that.a && b == that.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
