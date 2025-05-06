package com.sw.yutnori.board;

import java.util.*;

public class BoardLayoutCalculator {
    public static List<Node> createNodes(String boardType, int width, int height) {
        switch (boardType.toLowerCase()) {
            case "square": return createPolygonBoard(4, width, height, "square");
            case "pentagon": return createPolygonBoard(5, width, height, "pentagon");
            case "hexagon": return createPolygonBoard(6, width, height, "hexagon");
            default: return createPolygonBoard(4, width, height, "square");
        }
    }

    // 다각형 판 생성
    private static List<Node> createPolygonBoard(int vertexCount, int width, int height, String shape) {
        List<Node> nodes = new ArrayList<>();
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double baseRadius = Math.min(width, height) * 0.30;
        double radius = baseRadius;
        if (!"square".equals(shape)) {
            radius = baseRadius * (Math.sin(Math.PI / 4) / Math.sin(Math.PI / vertexCount));
        }
        Node centerNode = new Node("center", centerX, centerY, NodeType.CENTER);
        nodes.add(centerNode);

        // 꼭짓점 노드 생성 (등각 분할)
        Node[] corners = new Node[vertexCount];
        int corner0Index = -1;
        double maxY = Double.NEGATIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        // 꼭짓점 좌표 계산 (사각형은 직접 (사각형 shape), 나머지는 등분각)
        double[] xs = new double[vertexCount];
        double[] ys = new double[vertexCount];
        if ("square".equals(shape)) {
            xs[0] = centerX + radius; ys[0] = centerY + radius;
            xs[1] = centerX - radius; ys[1] = centerY + radius;
            xs[2] = centerX - radius; ys[2] = centerY - radius;
            xs[3] = centerX + radius; ys[3] = centerY - radius;
        } else {
            double[] angles = new double[vertexCount];
            for (int i = 0; i < vertexCount; i++) {
                angles[i] = 2 * Math.PI * i / vertexCount - Math.PI / 2;
            }
            for (int i = 0; i < vertexCount; i++) {
                xs[i] = centerX + radius * Math.cos(angles[i]);
                ys[i] = centerY + radius * Math.sin(angles[i]);
            }
        }
        // corner0Index 계산
        if ("hexagon".equals(shape)) {
            // 가장 오른쪽 아래 CORNER를 시작점(POINT)으로 설정정
            double maxSum = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < vertexCount; i++) {
                double sum = xs[i] + ys[i];
                if (sum > maxSum) {
                    maxSum = sum;
                    corner0Index = i;
                }
            }
        } else {
            for (int i = 0; i < vertexCount; i++) {
                double x = xs[i];
                double y = ys[i];
                // y가 가장 크고, x가 가장 큰 꼭짓점 찾기 (corner0)
                if (y > maxY || (Math.abs(y - maxY) < 1e-6 && x > maxX)) {
                    maxY = y;
                    maxX = x;
                    corner0Index = i;
                }
            }
        }
        // corner0부터 반시계방향으로 증가하도록 노드 
        // !TODO: GUI 더 깔끔하게 수정
        for (int i = 0; i < vertexCount; i++) {
            int idx = (corner0Index - i + vertexCount) % vertexCount;
            String id = "corner" + i;
            if ("hexagon".equals(shape) && i == 0) {
                corners[i] = new Node(id, xs[idx], ys[idx], NodeType.CORNER, NodeType.POINT);
            } else if (!"hexagon".equals(shape) && i == 0) {
                corners[i] = new Node(id, xs[idx], ys[idx], NodeType.CORNER, NodeType.POINT);
            } else {
                corners[i] = new Node(id, xs[idx], ys[idx], NodeType.CORNER);
            }
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

        return nodes;
    }

}
