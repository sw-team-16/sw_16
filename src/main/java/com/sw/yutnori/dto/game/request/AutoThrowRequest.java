package com.sw.yutnori.dto.game.request;

import com.sw.yutnori.common.enums.YutResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AutoThrowRequest {

    @Schema(description = "턴 ID", example = "12", required = true)
    private Long turnId;

    @Schema(description = "플레이어 ID", example = "1", required = true)
    private Long playerId;

}
