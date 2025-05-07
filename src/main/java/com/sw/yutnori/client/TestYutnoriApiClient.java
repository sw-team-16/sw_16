package com.sw.yutnori.client;

import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestYutnoriApiClient extends YutnoriApiClient {
    private final Random random = new Random();

    @Override
    public AutoThrowResponse getRandomYutResult(Long gameId, Long turnId, Long playerId) {
        YutResult[] values = YutResult.values();
        YutResult result = values[random.nextInt(values.length)];

        AutoThrowResponse response = new AutoThrowResponse(result, turnId + 1);

        return response;
    }

}