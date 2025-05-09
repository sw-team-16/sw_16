package com.sw.yutnori.board;

import com.sw.yutnori.common.LogicalPosition;

import java.util.ArrayList;
import java.util.List;

public class BoardPathManager {

    public List<LogicalPosition> computePath(LogicalPosition start, String yutResult, String boardType) {
        int steps = getStepCount(yutResult);

        if (boardType.equalsIgnoreCase("square")) {
            return computePathSquare(start, steps);
        } else if (boardType.equalsIgnoreCase("pentagon")) {
            return computePathPolygon(start, steps, 5);
        } else if (boardType.equalsIgnoreCase("hexagon")) {
            return computePathPolygon(start, steps, 6);
        } else {
            throw new IllegalArgumentException("Unsupported board type: " + boardType);
        }
    }

    private List<LogicalPosition> computePathSquare(LogicalPosition start, int steps) {
        return computePathPolygon(start, steps, 4);
    }

    private List<LogicalPosition> computePathPolygon(LogicalPosition start, int steps, int vertexCount) {
        List<LogicalPosition> path = new ArrayList<>();
        int a = start.getA();
        int b = start.getB();

        for (int i = 0; i < steps; i++) {
            if (a == 5 && b <= vertexCount) {
                // 분기점에서 지름길로 진입
                b = vertexCount * 10;
                a = 1;
            } else if (b % 10 == 0) {
                // 지름길 진행 중
                a++;
            } else {
                // 바깥 원 경로 진행 중
                if (a < 5) {
                    a++;
                } else {
                    a = 1;
                    b = (b % vertexCount) + 1;
                }
            }
            path.add(new LogicalPosition(null, a, b));
        }

        return path;
    }

    private int getStepCount(String yutResult) {
        return switch (yutResult.toUpperCase()) {
            case "도" -> 1;
            case "개" -> 2;
            case "걸" -> 3;
            case "윷" -> 4;
            case "모" -> 5;
            case "빽도" -> -1;
            default -> throw new IllegalArgumentException("Invalid yut result: " + yutResult);
        };
    }
}
