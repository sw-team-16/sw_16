package com.sw.yutnori.client;

import com.sw.yutnori.dto.game.request.AutoThrowRequest;
import com.sw.yutnori.dto.game.request.ManualThrowRequest;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;
import com.sw.yutnori.dto.piece.response.MovablePieceResponse;
import com.sw.yutnori.common.enums.YutResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    // 말 관련 - 별도 구현 필요
    public List<MovablePieceResponse> getMovablePieces(Long playerId) {
        String url = baseUrl + "/game/player/" + playerId + "/movable-pieces";
        MovablePieceResponse[] response = restTemplate.getForObject(url, MovablePieceResponse[].class);
        return response != null ? List.of(response) : Collections.emptyList();
    }
}