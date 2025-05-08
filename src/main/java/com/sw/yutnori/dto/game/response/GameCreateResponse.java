package com.sw.yutnori.dto.game.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GameCreateResponse {
    private Long gameId;
    private List<PlayerInfo> players;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PlayerInfo {
        private Long playerId;
        private String name;
        private String color;
    }
}
