package com.sw.yutnori.dto.game.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameWinnerResponse {
    private Long winnerPlayerId;
    private String winnerName;
}
