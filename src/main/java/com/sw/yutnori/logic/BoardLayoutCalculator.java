/*
 * BoardLayoutLayoutCalculator.java
 * Node들을 통해 윷판 모양에 따라 윷판의 레이아웃 계산
 * 
 * 
 */
package com.sw.yutnori.logic;

import java.util.*;

import com.sw.yutnori.model.Node;

public class BoardLayoutCalculator {
    public static List<Node> createNodes(String boardType, int width, int height) {
        switch (boardType.toLowerCase()) {
            case "square": return createPolygonBoard(4, width, height, "square");
            case "pentagon": return createPolygonBoard(5, width, height, "pentagon");
            case "hexagon": return createPolygonBoard(6, width, height, "hexagon");
            default: return createPolygonBoard(4, width, height, "square");
        }
    }

    // 다각형 판 생성 (a, b: 논리좌표 / x, y: 실제 위치)
    private static List<Node> createPolygonBoard(int vertexCount, int width, int height, String shape) {
        List<Node> nodes = new ArrayList<>();
        Map<String, Node> nodeMap = new HashMap<>();

        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double baseRadius = Math.min(width, height) * 0.30;
        double radius = baseRadius;

        // 사각형 모양 판은 직접 계산, 다른 모양은 내접원의 반지름 사용
        if (!"square".equals(shape)) {
            radius = baseRadius * (Math.sin(Math.PI / 4) / Math.sin(Math.PI / vertexCount));
        }

        // CenterNode (3,10)
        Node centerNode = new Node(3, 10, centerX, centerY);
        nodes.add(centerNode);
        nodeMap.put("0,0", centerNode);

        // 꼭짓점 노드 생성 (등각 분할)
        Node[] corners = new Node[vertexCount];
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

        // 시작/종료 지점 노드 찾기
        double maxY = Double.NEGATIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        int corner0Index = -1;
        if ("hexagon".equals(shape)) {
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
                if (y > maxY || (Math.abs(y - maxY) < 1e-6 && x > maxX)) {
                    maxY = y;
                    maxX = x;
                    corner0Index = i;
                }
            }
        }

        // corners[0]에 corner0Index 위치의 꼭짓점 노드의 논리 좌표를 (0,1)로 고정
        corners[0] = new Node(0, 1, xs[corner0Index], ys[corner0Index]);
        nodes.add(corners[0]);
        nodeMap.put("0,1", corners[0]);


        // 나머지 꼭짓점 노드 생성 (논리좌표: (5, i), i=1~vertexCount-1)
        for (int i = 1; i < vertexCount; i++) {
            int idx = (corner0Index - i + vertexCount) % vertexCount;
            corners[i] = new Node(5, i, xs[idx], ys[idx]); // 논리좌표: (5, i)
            nodes.add(corners[i]);
            nodeMap.put("5," + i, corners[i]);
        }

        // 각 변마다 일반 노드 생성 - 각 변에 4개씩
        for (int i = 0; i < vertexCount; i++) {
            Node current = (i == 0) ? corners[0] : corners[i];
            Node next = (i == vertexCount - 1) ? corners[0] : corners[i + 1];
            for (int j = 1; j <= 4; j++) {
                double ratio = j / 5.0;
                double x = current.getX() + (next.getX() - current.getX()) * ratio;
                double y = current.getY() + (next.getY() - current.getY()) * ratio;
                Node edgeNode = new Node(j, i + 1, x, y); // 논리좌표: (j, i+1)
                nodes.add(edgeNode);
                nodeMap.put(j + "," + (i + 1), edgeNode);

                if (j == 1) {
                    current.addConnection(edgeNode);
                    edgeNode.addConnection(current);
                } else {
                    Node prevNode = nodeMap.get((j - 1) + "," + (i + 1));
                    prevNode.addConnection(edgeNode);
                    edgeNode.addConnection(prevNode);
                }
                if (j == 4) {
                    next.addConnection(edgeNode);
                    edgeNode.addConnection(next);
                }
            }
        }

        // 대각선(지름길) 노드 생성 - 각 꼭짓점마다 2개씩
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 1; j <= 2; j++) {
                double ratio = j / 3.0;
                double x = centerNode.getX() + (corners[i].getX() - centerNode.getX()) * ratio;
                double y = centerNode.getY() + (corners[i].getY() - centerNode.getY()) * ratio;
                int logicA = (i == 0) ? vertexCount * 10 : i * 10;
                Node diagonalNode = new Node(logicA, 3-j, x, y); // 논리좌표: 첫번째 지름길 한정 (vertexCount*10, j), 나머지는 (i*10, j)
                nodes.add(diagonalNode);
                nodeMap.put(logicA + "," + j, diagonalNode);

                if (j == 1) {
                    centerNode.addConnection(diagonalNode);
                    diagonalNode.addConnection(centerNode);
                } else {
                    Node prevNode = nodeMap.get(logicA + "," + (j-1));
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
