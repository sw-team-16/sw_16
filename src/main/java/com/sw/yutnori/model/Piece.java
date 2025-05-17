package com.sw.yutnori.model;

import com.sw.yutnori.model.enums.PieceState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Piece {
    private Long PieceId;
    private Player player;
    private int a;
    private int b;
    private boolean isFinished = false;
    private boolean isGrouped = false;
    private Long groupId;
    private PieceState state = PieceState.READY;

    public void setLogicalPosition(int a, int b) {
        this.a = a;
        this.b = b;
    }
}