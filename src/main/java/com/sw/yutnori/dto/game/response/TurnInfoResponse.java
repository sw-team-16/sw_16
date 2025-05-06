package com.sw.yutnori.dto.game.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TurnInfoResponse {
    private Long turnId;
    private Long playerId;
    private String playerName;
    private String result;
    private Long chosenPieceId;
    private boolean isUsed;
}
