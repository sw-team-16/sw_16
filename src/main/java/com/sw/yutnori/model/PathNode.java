package com.sw.yutnori.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathNode {
    private Long id;
    private Board board;
    private int a;
    private int b;
    private int x;
    private int y;
    private boolean isCenter = false;
    private boolean isStartOrEnd = false;
}
