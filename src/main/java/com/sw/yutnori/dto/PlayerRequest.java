package com.sw.yutnori.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 참가자 등록 요청 DTO")
public class PlayerRequest {

    @Schema(description = "사용자 ID", example = "2", required = true)
    private Long userId;

    @Schema(description = "팀 ID", example = "1", required = true)
    private Long teamId;

    // Getter & Setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
