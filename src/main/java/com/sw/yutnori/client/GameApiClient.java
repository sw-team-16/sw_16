package com.sw.yutnori.client;

import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;

public interface GameApiClient {

    // S2-1 : 윷 랜덤 던지기
    AutoThrowResponse getRandomYutResult(Long gameId, Long turnId, Long playerId);

    // S2-2 : 수동(지정) 윷 던지기
    void throwYutManual(Long gameId, Long turnId, Long playerId, Long pieceId, YutResult result);

}
