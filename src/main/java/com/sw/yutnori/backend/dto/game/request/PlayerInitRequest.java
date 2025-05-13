package com.sw.yutnori.backend.dto.game.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerInitRequest {
    @Schema(description = "플레이어 이름", example = "Alice", required = true)
    private String name;

    @Schema(description = "플레이어 색상", example = "RED", required = true)
    private String color;


}