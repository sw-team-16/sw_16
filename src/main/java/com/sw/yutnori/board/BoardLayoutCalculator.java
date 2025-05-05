package com.sw.yutnori.board;

import java.util.*;

public class BoardLayoutCalculator {
    public static List<Node> createNodes(String boardType, int width, int height) {
        switch (boardType.toLowerCase()) {
            case "square": return createPolygonBoard(4, width, height);
            case "pentagon": return createPolygonBoard(5, width, height);
            case "hexagon": return createPolygonBoard(6, width, height);
            default: return createPolygonBoard(4, width, height);
        }
    }

    // 다각형 판 생성
    private static List<Node> createPolygonBoard(int vertexCount, int width, int height) {
        List<Node> nodes = new ArrayList<>();
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double radius = Math.min(width, height) * 0.35;
        Node centerNode = new Node("center", centerX, centerY, NodeType.CENTER);
        nodes.add(centerNode);

        // 꼭짓점 노드 생성 (등각 분할)
        Node[] corners = new Node[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            double angle = 2 * Math.PI * i / vertexCount - Math.PI / 2;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            corners[i] = new Node("corner" + i, x, y, NodeType.CORNER);
            nodes.add(corners[i]);
        }

        // 각 변마다 일반 노드 생성 - 각 변에 4개씩
        for (int i = 0; i < vertexCount; i++) {
            Node current = corners[i];
            Node next = corners[(i + 1) % vertexCount];
            for (int j = 1; j <= 4; j++) {
                double ratio = j / 5.0;
                double x = current.getX() + (next.getX() - current.getX()) * ratio;
                double y = current.getY() + (next.getY() - current.getY()) * ratio;
                Node edgeNode = new Node("edge_" + i + "_" + j, x, y, NodeType.REGULAR);
                nodes.add(edgeNode);
                if (j == 1) {
                    current.addConnection(edgeNode);
                    edgeNode.addConnection(current);
                } else {
                    Node prevNode = nodes.get(nodes.size() - 2);
                    prevNode.addConnection(edgeNode);
                    edgeNode.addConnection(prevNode);
                }
                if (j == 4) {
                    edgeNode.addConnection(next);
                    next.addConnection(edgeNode);
                }
            }
        }

        // 대각선(지름길) 노드 생성 - 각 꼭짓점마다 2개씩
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 1; j <= 2; j++) {
                double ratio = j / 3.0;
                double x = centerNode.getX() + (corners[i].getX() - centerNode.getX()) * ratio;
                double y = centerNode.getY() + (corners[i].getY() - centerNode.getY()) * ratio;
                Node diagonalNode = new Node("diagonal_" + i + "_" + j, x, y, NodeType.SHORTCUT);
                nodes.add(diagonalNode);
                if (j == 1) {
                    centerNode.addConnection(diagonalNode);
                    diagonalNode.addConnection(centerNode);
                } else {
                    Node prevNode = nodes.get(nodes.size() - 2);
                    prevNode.addConnection(diagonalNode);
                    diagonalNode.addConnection(prevNode);
                    diagonalNode.addConnection(corners[i]);
                    corners[i].addConnection(diagonalNode);
                }
            }
        }

        // 시작/종료점(POINT) 추가 (아래쪽 변 중간)
        Node pointNode = new Node("point", centerX, centerY + radius + 50, NodeType.POINT, NodeType.CORNER);
        nodes.add(pointNode);
        Node connectToPoint = findNodeById(nodes, "edge_" + (vertexCount/2) + "_2");
        if (connectToPoint != null) {
            pointNode.addConnection(connectToPoint);
            connectToPoint.addConnection(pointNode);
        }
        return nodes;
    }

    private static Node findNodeById(List<Node> nodes, String id) {
        for (Node node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }
}
