package com.sw.yutnori.model;

import com.sw.yutnori.model.enums.YutResult;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnAction {
    private Long id;
    private Turn turn;
    private int moveOrder;
    private boolean isUsed = false;
    private Piece chosenPiece;
    private YutResult result;
}
