package com.sw.yutnori.client;

import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;

import java.util.Random;

// 임시 테스트용
public class TestYutnoriApiClient extends YutnoriApiClient {
    private final Random random = new Random();

    @Override
    public AutoThrowResponse getRandomYutResult(Long gameId, Long turnId, Long playerId) {
        YutResult[] values = YutResult.values();
        YutResult result = values[random.nextInt(values.length)];

        return new AutoThrowResponse(result, turnId + 1);
    }

}