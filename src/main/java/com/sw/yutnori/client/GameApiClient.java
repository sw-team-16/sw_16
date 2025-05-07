package com.sw.yutnori.client;

import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;

public interface GameApiClient {

    AutoThrowResponse getRandomYutResult(Long gameId, Long turnId, Long playerId);

    void throwYutManual(Long gameId, Long turnId, Long playerId, Long pieceId, YutResult result);

}
