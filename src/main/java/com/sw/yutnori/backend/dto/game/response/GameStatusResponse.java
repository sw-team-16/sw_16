package com.sw.yutnori.backend.dto.game.response;

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
        private int x;
        private int y;
        private boolean isFinished;
        private int a;
        private int b;

        /**
         * (a, b): 논리 좌표 (메인 로직용)
         * (x, y): 실제 좌표 (DB/GUI용)
         */
    }
}
