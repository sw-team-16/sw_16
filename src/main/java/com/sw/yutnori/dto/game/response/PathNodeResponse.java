package com.sw.yutnori.dto.game.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PathNodeResponse {
    private Long nodeId;
    private int xCoord;
    private int yCoord;
    private boolean isCenter;
    private boolean isStartOrEnd;
    private Long nextNodeId;
}