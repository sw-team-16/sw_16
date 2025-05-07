package com.sw.yutnori.dto.game.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GameStatusResponse {
    private Long gameId;
    private String state;
    private String boardType;
    private int numPlayers;
    private int numPieces;
    private Long currentTurnPlayerId;
    private List<PieceInfo> pieces;

    @Getter
    @AllArgsConstructor
    public static class PieceInfo {
        private Long pieceId;
        private Long playerId;
        private int xCoord;
        private int yCoord;
        private boolean isFinished;
    }
}
