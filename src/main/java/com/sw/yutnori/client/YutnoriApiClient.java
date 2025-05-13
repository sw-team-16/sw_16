
/*
* GameApiClient 및 YutnoriApiClient 제거
RestTemplate 기반 API 호출 제거

GameManager 로컬 클래스에서 직접 로직 수행하도록 변경
* */
package com.sw.yutnori.client;

import com.sw.yutnori.backend.dto.game.request.AutoThrowRequest;
import com.sw.yutnori.backend.dto.game.request.ManualThrowRequest;
import com.sw.yutnori.backend.dto.game.request.MovePieceRequest;
import com.sw.yutnori.backend.dto.game.request.RestartGameRequest;
import com.sw.yutnori.backend.dto.game.response.AutoThrowResponse;
import com.sw.yutnori.model.enums.YutResult;
import com.sw.yutnori.backend.dto.game.response.MovePieceResponse;
import com.sw.yutnori.backend.dto.game.response.GameStatusResponse;
import org.springframework.web.client.RestTemplate;
import com.sw.yutnori.backend.dto.game.response.TurnInfoResponse;


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
    @Override
    public MovePieceResponse movePiece(Long gameId, MovePieceRequest request) {
        String url = baseUrl + "/api/game/" + gameId + "/move";
        return restTemplate.postForObject(url, request, MovePieceResponse.class);
    }


    @Override
    public TurnInfoResponse getTurnInfo(Long gameId) {
        String url = baseUrl + "/api/game/" + gameId + "/turn";
        return restTemplate.getForObject(url, TurnInfoResponse.class);
    }

    @Override
    public GameStatusResponse getGameStatus(Long gameId) {
        String url = baseUrl + "/api/game/" + gameId + "/status";
        return restTemplate.getForObject(url, GameStatusResponse.class);
    }

    // 이하 나머지 API 클라이언트 구현 필요
}