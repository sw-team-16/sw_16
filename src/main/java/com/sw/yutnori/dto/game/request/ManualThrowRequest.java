package com.sw.yutnori.dto.game.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManualThrowRequest {
    @Schema(description = "플레이어 id", example = "1", required = true)
    private Long playerId;
    @Schema(description = "결과 : ( BACK_DO, DO, GAE, GEOL, YUT, MO) 중 하나", example = "Player1", required = true)
    private String result; // BACK_DO, DO, GAE, GEOL, YUT, MO
}

