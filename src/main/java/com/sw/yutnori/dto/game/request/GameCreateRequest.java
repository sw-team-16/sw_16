package com.sw.yutnori.dto.game.request;

import com.sw.yutnori.common.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateRequest {
    @Schema(description = "보드 타입", example = "SQUARE", required = true)
    private BoardType boardType;

    @Schema(description = "게임 참여 플레이어 목록", required = true)
    private List<PlayerInitRequest> players;

    @Schema(description = "인당 말 개수", example = "3", required = true)
    private int numPieces;

}
