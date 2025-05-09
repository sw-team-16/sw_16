package com.sw.yutnori.client;

import com.sw.yutnori.dto.game.request.AutoThrowRequest;
import com.sw.yutnori.dto.game.request.ManualThrowRequest;
import com.sw.yutnori.dto.game.request.RestartGameRequest;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;
import com.sw.yutnori.common.enums.YutResult;
import org.springframework.web.client.RestTemplate;

public class YutnoriApiClient implements GameApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080";

    public YutnoriApiClient() {
        this.restTemplate = new RestTemplate();
    }

    // S2-1 : 윷 랜덤 던지기
    @Override
    public AutoThrowResponse getRandomYutResult(Long gameId, Long turnId, Long playerId) {
        AutoThrowRequest request = new AutoThrowRequest(playerId);

        String url = baseUrl + "/api/game/" + gameId + "/turn/random/throw";
        return restTemplate.postForObject(url, request, AutoThrowResponse.class);
    }

    // S2-2 : 수동(지정) 윷 던지기
    @Override
    public void throwYutManual(Long gameId, Long turnId, Long playerId, Long pieceId, YutResult result) {
        ManualThrowRequest request = new ManualThrowRequest( playerId, pieceId, result);

        String url = baseUrl + "/api/game/" + gameId + "/turn/manual/throw";
        restTemplate.postForObject(url, request, Void.class);
    }

    // 게임 재시작 API 호출
    @Override
    public void restartGame(Long gameId, Long winnerPlayerId) {
        RestartGameRequest request = new RestartGameRequest(winnerPlayerId);
        String url = baseUrl + "/api/game/" + gameId + "/restart";
        restTemplate.postForObject(url, request, Void.class);
    }

    // 이하 나머지 API 클라이언트 구현 필요
}