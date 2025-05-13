/*
 * RestartGameRequest.java
 * 게임 재시작 요청 클래스
 * 
 * 
 */
package com.sw.yutnori.backend.dto.game.request;

import lombok.Getter;

@Getter
public class RestartGameRequest {
    private Long winnerPlayerId;

    public RestartGameRequest(Long winnerPlayerId) {
        this.winnerPlayerId = winnerPlayerId;
    }
}