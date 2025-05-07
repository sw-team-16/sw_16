package com.sw.yutnori.dto.game.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게임 참가자 등록 요청 DTO")
public class PlayerRequest {
    @Schema(description = "플레이어 이름", example = "Player1", required = true)
    private String name;

    @Schema(description = "말 색상", example = "RED", required = true)
    private String color;

    @Schema(description = "말 개수", example = "4", required = true)
    private int numOfPieces;
}
