package com.sw.yutnori.backend.dto.game.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameWinnerResponse {
    private Long winnerPlayerId;
    private String winnerName;
}
