package com.sw.yutnori.board;

import com.sw.yutnori.common.LogicalPosition;
import com.sw.yutnori.common.enums.BoardType;
import com.sw.yutnori.common.enums.YutResult;

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

    public static LogicalPosition calculateDestination(
            Long pieceId,
            int currentA,
            int currentB,
            YutResult result,
            BoardType boardType
    ) {
        return switch (boardType) {
            case SQUARE -> calculateSQUARE(pieceId, currentA, currentB, result);
            case PENTAGON -> calculatePENTAGON(pieceId, currentA, currentB, result);
            case HEXAGON -> calculateHEXAGON(pieceId, currentA, currentB, result);
        };
    }

    private static LogicalPosition calculateSQUARE(Long pieceId, int a, int b, YutResult result) {
        int steps = switch (result) {
            case DO -> 1;
            case GAE -> 2;
            case GEOL -> 3;
            case YUT -> 4;
            case MO -> 5;
            case BACK_DO -> -1;
        };

        if (a == 5 && b == 1) {
            return switch (result) {
                case DO -> new LogicalPosition(pieceId, 1, 10);
                case GAE -> new LogicalPosition(pieceId, 2, 10);
                case GEOL -> new LogicalPosition(pieceId, 3, 10);
                case YUT -> new LogicalPosition(pieceId, 2, 30);
                case MO -> new LogicalPosition(pieceId, 1, 30);
                case BACK_DO -> new LogicalPosition(pieceId, 4, 1);
            };
        } else if (a == 5 && b == 2) {
            return switch (result) {
                case DO -> new LogicalPosition(pieceId, 1, 20);
                case GAE -> new LogicalPosition(pieceId, 2, 20);
                case GEOL -> new LogicalPosition(pieceId, 3, 10);
                case YUT -> new LogicalPosition(pieceId, 2, 40);
                case MO -> new LogicalPosition(pieceId, 1, 40);
                case BACK_DO -> new LogicalPosition(pieceId, 4, 2);
            };
        } else if (a == 3 && b == 10) {
            return switch (result) {
                case DO -> new LogicalPosition(pieceId, 2, 40);
                case GAE -> new LogicalPosition(pieceId, 1, 40);
                case GEOL, YUT, MO -> new LogicalPosition(pieceId, 0, 1);
                case BACK_DO -> new LogicalPosition(pieceId, 2, 10);
            };
        }

        int[][] mainPath = {
                {0,1}, {1,1}, {2,1}, {3,1}, {4,1}, {5,1},
                {1,2}, {2,2}, {3,2}, {4,2}, {5,2},
                {1,3}, {2,3}, {3,3}, {4,3}, {5,3},
                {1,4}, {2,4}, {3,4}, {4,4}, {0,1}
        };

        for (int i = 0; i < mainPath.length; i++) {
            if (mainPath[i][0] == a && mainPath[i][1] == b) {
                int newIndex = i + steps;
                if (newIndex >= mainPath.length) newIndex = mainPath.length - 1;
                if (newIndex < 0) newIndex = 0;
                return new LogicalPosition(pieceId, mainPath[newIndex][0], mainPath[newIndex][1]);
            }
        }

        return new LogicalPosition(pieceId, a, b);
    }

    private static LogicalPosition calculatePENTAGON(Long pieceId, int a, int b, YutResult result) {
        return new LogicalPosition(pieceId, 4, 9);
    }

    private static LogicalPosition calculateHEXAGON(Long pieceId, int a, int b,YutResult result) {
        return new LogicalPosition(pieceId, 5, 8);
    }


}
