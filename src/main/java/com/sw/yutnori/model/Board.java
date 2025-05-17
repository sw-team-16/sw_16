/*
 * Board.java
 * 윷판 모델
 * 
 * 
 * 
 */
package com.sw.yutnori.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import com.sw.yutnori.logic.BoardLayoutCalculator;

@Getter
@Setter
public class Board {
    private Long id;
    private Game game;

    private String boardType;
    private int width;
    private int height;
    private List<Node> nodes;

    public Board() {}

    public Board(String boardType, int width, int height) {
        this.boardType = boardType;
        this.width = width;
        this.height = height;
        this.nodes = BoardLayoutCalculator.createNodes(boardType, width, height);
    }

    public Node findNode(int a, int b) {
        if (nodes == null) return null;
        return nodes.stream()
                .filter(n -> n.getA() == a && n.getB() == b)
                .findFirst()
                .orElse(null);
    }
}