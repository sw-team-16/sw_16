package com.sw.yutnori.service;

import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.GameStatusResponse;
import com.sw.yutnori.dto.game.response.GameWinnerResponse;
import com.sw.yutnori.dto.game.response.TurnInfoResponse;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;
import com.sw.yutnori.dto.game.response.YutThrowResponse;
import com.sw.yutnori.dto.piece.response.MovablePieceResponse;

import java.util.List;

public interface GameService {

    Long createGame(GameCreateRequest request);


    YutThrowResponse throwYutRandom(Long gameId, ManualThrowRequest request);

    void throwYutManual(Long gameId, ManualThrowRequest request);

    List<Long> getMovablePieces(Long gameId);


    void movePiece(MovePieceRequest request);

    GameStatusResponse getGameStatus(Long gameId);

    GameWinnerResponse getWinner(Long gameId);

    void deleteGame(Long gameId);

    void restartGame(Long gameId, Long winnerPlayerId);

    TurnInfoResponse getTurnInfo(Long gameId);


    AutoThrowResponse getRandomYutResultForPlayer(Long gameId, AutoThrowRequest request);

    YutThrowResponse applyRandomYutResult(Long gameId, AutoThrowApplyRequest request);
    List<MovablePieceResponse> getMovablePiecesByPlayer(Long playerId);


}

