package com.sw.yutnori.dto.game.request;

import com.sw.yutnori.common.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateRequest {
    @Schema(description = "보드 타입", example = " SQUARE | PENTAGON | HEXAGON", required = true)
    private BoardType boardType;
    @Schema(description = "게임 인원 수", example = "2", required = true)
    private int numPlayers;
    @Schema(description = "인당 말 갯 수", example = "3", required = true)
    private int numPieces;
}
