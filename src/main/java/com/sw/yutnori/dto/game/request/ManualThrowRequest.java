package com.sw.yutnori.dto.game.request;
import com.sw.yutnori.common.enums.YutResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManualThrowRequest {

    @Schema(description = "플레이어 ID", example = "1", required = true)
    private Long playerId;

    @Schema(description = "적용할 말 ID", example = "3", required = true)
    private Long pieceId;

    @Schema(description = "결과 : ( BACK_DO, DO, GAE, GEOL, YUT, MO) 중 하나", example = "Player1", required = true)
    private YutResult result;


}

