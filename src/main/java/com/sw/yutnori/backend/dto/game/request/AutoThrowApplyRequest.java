package com.sw.yutnori.backend.dto.game.request;

import com.sw.yutnori.model.enums.YutResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutoThrowApplyRequest {
    @Schema(description = "현재 turn id")
    private Long turnId;
    @Schema(description = "현재 플레이어 id")
    private Long playerId;
    @Schema(description = "적용할 말 id")
    private Long pieceId;
    @Schema(description = "백엔드에서 방금 반환해 준 결과 값 ")
    private YutResult result;
}