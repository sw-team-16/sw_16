package com.sw.yutnori.dto.game.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovePieceRequest {
    @Schema(description = "현재 턴 ID", example = "12")
    private Long turnId;

    @Schema(description = "선택한 말 ID", example = "5")
    private Long chosenPieceId;

    @Schema(description = "이동 순서 (첫 번째 사용된 결과부터)", example = "1")
    private int moveOrder;

    @Schema(description = "이동할 X 좌표", example = "0")
    private int xcoord;

    @Schema(description = "이동할 Y 좌표", example = "1")
    private int ycoord;
}
