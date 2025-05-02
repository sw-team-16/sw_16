package com.sw.yutnori.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 생성 요청 DTO")
public class GameCreateRequest {

    @Schema(description = "게임 이름", example = "유튜노리", required = true)
    private String gameName;

    @Schema(description = "방장 ID", example = "1", required = true)
    private Long hostId;

    // Getter & Setter
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }
}
