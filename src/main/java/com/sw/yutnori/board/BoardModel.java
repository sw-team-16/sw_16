/*
 * BoardModel.java
 * 윷놀이 판의 모델을 관리하는 클래스
 *  - 판 타입, 크기, 노드 리스트 관리
 * 
 * 
 */
package com.sw.yutnori.board;

import java.util.List;

public class BoardModel {
    private final String boardType;
    private final int width;
    private final int height;
    private final List<Node> nodes;

    public BoardModel(String boardType, int width, int height) {
        this.boardType = boardType;
        this.width = width;
        this.height = height;
        this.nodes = BoardLayoutCalculator.createNodes(boardType, width, height);
    }
    public Node findNode(int a, int b) {
        return nodes.stream()
                .filter(n -> n.getA() == a && n.getB() == b)
                .findFirst()
                .orElse(null);
    }


    public String getBoardType() { return boardType; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<Node> getNodes() { return nodes; }
} 