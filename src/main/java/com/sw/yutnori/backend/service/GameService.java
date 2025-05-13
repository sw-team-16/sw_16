package com.sw.yutnori.backend.service;

import com.sw.yutnori.backend.dto.game.request.*;
import com.sw.yutnori.backend.dto.game.response.*;
import com.sw.yutnori.backend.dto.piece.response.MovablePieceResponse;

import java.util.List;

public interface GameService {

    GameCreateResponse createGame(GameCreateRequest request);


    void throwYutManual(Long gameId, ManualThrowRequest request);


    MovePieceResponse movePiece(Long gameId, MovePieceRequest request);

    GameStatusResponse getGameStatus(Long gameId);

    GameWinnerResponse getWinner(Long gameId);

    void deleteGame(Long gameId);

    void restartGame(Long gameId, Long winnerPlayerId);

    TurnInfoResponse getTurnInfo(Long gameId);


    AutoThrowResponse getRandomYutResultForPlayer(Long gameId, AutoThrowRequest request);

    YutThrowResponse applyRandomYutResult(Long gameId, AutoThrowApplyRequest request);
    List<MovablePieceResponse> getMovablePiecesByPlayer(Long playerId);


}

