package com.sw.yutnori.board;

import java.util.*;

public class YutBoardTest {
    public static void main(String[] args) {
        BoardGraph board = new BoardGraph("pentagon", 800, 600);
        List<Node> nodes = board.getAllNodes();
        System.out.println("총 노드 수: " + nodes.size());
        Node pointNode = board.getNode("point");
        if (pointNode != null) {
            List<Node> movableNodes = board.getMovableNodes(pointNode, 4);
            System.out.println("시작/종료점에서 4칸 이동 가능 노드: " + movableNodes.size());
            for (Node node : movableNodes) {
                System.out.print(" - " + node.getId() + " [");
                for (NodeType t : node.getTypes()) System.out.print(t + ",");
                System.out.println("] (" + node.getX() + ", " + node.getY() + ")");
            }
        }
    }
}
