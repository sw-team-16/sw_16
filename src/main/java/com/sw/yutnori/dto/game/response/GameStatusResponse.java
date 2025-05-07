package com.sw.yutnori.dto.game.response;

import com.sw.yutnori.common.enums.BoardType;
import com.sw.yutnori.common.enums.GameState;
import com.sw.yutnori.domain.Game;
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

    public Game toGame() {
        Game game = new Game();
        game.setGameId(gameId);
        game.setState(GameState.valueOf(state));
        game.setBoardType(BoardType.valueOf(boardType));
        game.setNumPlayers(numPlayers);
        game.setNumPieces(numPieces);

        return game;
    }


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
