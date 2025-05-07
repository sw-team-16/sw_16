package com.sw.yutnori.client;

import com.sw.yutnori.dto.game.request.AutoThrowRequest;
import com.sw.yutnori.dto.game.request.ManualThrowRequest;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;
import com.sw.yutnori.common.enums.YutResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class YutnoriApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api";

    public YutnoriApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public AutoThrowResponse getRandomYutResult(Long gameId, Long turnId, Long playerId) {
        AutoThrowRequest request = new AutoThrowRequest(turnId, playerId);

        String url = baseUrl + "/game/" + gameId + "/turn/random";
        return restTemplate.postForObject(url, request, AutoThrowResponse.class);
    }

    public void throwYutManual(Long gameId, Long turnId, Long playerId, Long pieceId, YutResult result) {
        ManualThrowRequest request = new ManualThrowRequest(turnId, playerId, pieceId, result);

        String url = baseUrl + "/game/" + gameId + "/turn/manual";
        restTemplate.postForObject(url, request, Void.class);
    }

    public List<Long> getMovablePieces(Long gameId) {
        try {
            // 백엔드 연동 미구현
            // String url = baseUrl + "/game/" + gameId + "/movable-pieces";
            // return restTemplate.getForObject(url, List.class);

            // 임시로 고정된 값 반환
            System.out.println("임시 구현: 이동 가능한 말 목록 반환");
            return Arrays.asList(1L, 2L, 3L, 4L);
        } catch (Exception e) {
            System.err.println("이동 가능한 말 정보 조회 실패: " + e.getMessage());
            // 오류 발생 시 빈 목록 반환
            return Collections.emptyList();
        }
    }
}
