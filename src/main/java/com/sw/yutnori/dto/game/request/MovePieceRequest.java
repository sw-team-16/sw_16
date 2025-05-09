package com.sw.yutnori.dto.game.request;

import com.sw.yutnori.common.enums.YutResult;
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

    @Schema(description = "이동할 논리 좌표 a", example = "3")
    private int a;

    @Schema(description = "이동할 논리 좌표 b", example = "10")
    private int b;

    @Schema(description = "논리좌표 a")
    private int acoord;

    @Schema(description = "논리좌표 b")
    private int bcoord;

    @Schema(description = "윷 결과", example = "GEOL")
    private YutResult result;


}
