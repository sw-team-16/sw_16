package com.sw.yutnori.client;

import com.sw.yutnori.model.enums.YutResult;
import com.sw.yutnori.backend.dto.game.request.MovePieceRequest;
import com.sw.yutnori.backend.dto.game.response.AutoThrowResponse;
import com.sw.yutnori.backend.dto.game.response.MovePieceResponse;
import com.sw.yutnori.backend.dto.game.response.GameStatusResponse;
import com.sw.yutnori.backend.dto.game.response.TurnInfoResponse;

public interface GameApiClient {

    // S2-1 : 윷 랜덤 던지기
    AutoThrowResponse getRandomYutResult(Long gameId, Long turnId, Long playerId);

    // S2-2 : 수동(지정) 윷 던지기
    void throwYutManual(Long gameId, Long turnId, Long playerId, Long pieceId, YutResult result);

    void restartGame(Long gameId, Long winnerPlayerId);
    MovePieceResponse movePiece(Long gameId, MovePieceRequest request);

    TurnInfoResponse getTurnInfo(Long gameId);
    GameStatusResponse getGameStatus(Long gameId);


}
