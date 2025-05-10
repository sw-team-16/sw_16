package com.sw.yutnori.dto.game.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovePieceResponse {

    @Schema(description = "상대방 말 잡음 여부", example = "true")
    private boolean captureOccurred;

    @Schema(description = "우리 편 말 업기 여부", example = "true")
    private boolean groupingOccurred;

    @Schema(description = "말이 골인했는지 여부", example = "true")
    private boolean reachedEndPoint;

    @Schema(description = "추가 이동이 필요한지 여부", example = "true")
    private boolean requiresAnotherMove;

}
