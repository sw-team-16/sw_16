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

        // @AllArgsConstructor가 있으므로 모든 필드를 초기화하는 생성자 사용
        AutoThrowResponse response = new AutoThrowResponse(result, turnId + 1);

        System.out.println("테스트용 윷 결과: " + result);
        return response;
    }

    @Override
    public List<Long> getMovablePieces(Long gameId) {
        System.out.println("테스트용 이동 가능한 말 목록 반환");
        return Arrays.asList(1L, 2L, 3L, 4L);
    }

    // 필요한 다른 메서드들 오버라이드
}