package com.sw.yutnori.model;

import com.sw.yutnori.model.enums.PieceState;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player {
    private Long id;
    private String name;
    private String color;
    private int numOfPieces;
    private int finishedCount = 0;
    private Game game;
    private List<Piece> pieces = new ArrayList<>();
    public void addPiece(Piece piece) {
        if (pieces == null) {
            pieces = new ArrayList<>();
        }
        pieces.add(piece);
    }
    public int getFinishedCount() {
        return (int) pieces.stream()
                .filter(p -> p.getState() == PieceState.FINISHED)
                .count();
    }


}