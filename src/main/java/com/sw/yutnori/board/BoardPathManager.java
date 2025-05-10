package com.sw.yutnori.board;

import com.sw.yutnori.common.LogicalPosition;
import com.sw.yutnori.common.enums.BoardType;
import com.sw.yutnori.common.enums.YutResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.coyote.http11.Constants.a;

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
            int prevA,
            int prevB,
            YutResult result,
            BoardType boardType
    ) {
        return switch (boardType) {
            case SQUARE -> calculateSQUARE(pieceId, currentA, currentB, prevA, prevB, result);
            case PENTAGON -> calculatePENTAGON(pieceId, currentA, currentB, prevA, prevB, result);
            case HEXAGON -> calculateHEXAGON(pieceId, currentA, currentB, prevA, prevB, result);
        };
    }

    public static LogicalPosition calculateSQUARE(
            Long pieceId,
            int a,
            int b,
            int prevA,
            int prevB,
            YutResult result) {
        int step = result.getStepCount();  // 각 윷 결과에 따른 이동 수

        List<LogicalPosition> path;

        if (a == 5 && b == 1) {
            path = List.of(
                    new LogicalPosition(pieceId, 1, 10),
                    new LogicalPosition(pieceId, 2, 10),
                    new LogicalPosition(pieceId, 3, 10),
                    new LogicalPosition(pieceId, 2, 30),
                    new LogicalPosition(pieceId, 1, 30),
                    new LogicalPosition(pieceId, 5, 3)
            );
        } else if (a == 5 && b == 2) {
            path = List.of(
                    new LogicalPosition(pieceId, 1, 20),
                    new LogicalPosition(pieceId, 2, 20),
                    new LogicalPosition(pieceId, 3, 10),
                    new LogicalPosition(pieceId, 2, 40),
                    new LogicalPosition(pieceId, 1, 40),
                    new LogicalPosition(pieceId, 3, 30),
                    new LogicalPosition(pieceId, 0, 1)
            );
        } else if (a == 3 && b == 10 && prevA == 5 && prevB == 1) {
            path = List.of(
                    new LogicalPosition(pieceId, 2, 40),
                    new LogicalPosition(pieceId, 1, 40),
                    new LogicalPosition(pieceId, 0, 1)
            );
        } else {
            path = List.of(
                    new LogicalPosition(pieceId, 0, 1),
                    new LogicalPosition(pieceId, 1, 1),
                    new LogicalPosition(pieceId, 2, 1),
                    new LogicalPosition(pieceId, 3, 1),
                    new LogicalPosition(pieceId, 4, 1),
                    new LogicalPosition(pieceId, 5, 1),
                    new LogicalPosition(pieceId, 1, 2),
                    new LogicalPosition(pieceId, 2, 2),
                    new LogicalPosition(pieceId, 3, 2),
                    new LogicalPosition(pieceId, 4, 2),
                    new LogicalPosition(pieceId, 5, 2),
                    new LogicalPosition(pieceId, 1, 3),
                    new LogicalPosition(pieceId, 2, 3),
                    new LogicalPosition(pieceId, 3, 3),
                    new LogicalPosition(pieceId, 4, 3),
                    new LogicalPosition(pieceId, 5, 3),
                    new LogicalPosition(pieceId, 1, 4),
                    new LogicalPosition(pieceId, 2, 4),
                    new LogicalPosition(pieceId, 3, 4),
                    new LogicalPosition(pieceId, 4, 4),
                    new LogicalPosition(pieceId, 0, 1)
            );
        }

        int currentIdx = -1;
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getA() == a && path.get(i).getB() == b) {
                currentIdx = i;
                break;
            }
        }

        int nextIdx = Math.min(currentIdx + step, path.size() - 1);
        return path.get(nextIdx);
    }


    private static LogicalPosition calculatePENTAGON(
            Long pieceId,
            int a,
            int b,
            int prevA,
            int prevB,
            YutResult result) {
        int step = yutResultToStep(result);

        // === 지름길 경로 정의 ===
        Map<String, LogicalPosition[]> shortcutRoutes = new HashMap<>();
        shortcutRoutes.put("5,1", new LogicalPosition[]{
                new LogicalPosition(pieceId, 1, 10),
                new LogicalPosition(pieceId, 2, 10),
                new LogicalPosition(pieceId, 3, 10),
                new LogicalPosition(pieceId, 1, 40),
                new LogicalPosition(pieceId, 2, 40),
                new LogicalPosition(pieceId, 5, 4),
                new LogicalPosition(pieceId, 1, 5),
                new LogicalPosition(pieceId, 2, 5),
                new LogicalPosition(pieceId, 3, 5),
                new LogicalPosition(pieceId, 4, 5),
                new LogicalPosition(pieceId, 0, 1)
        });
        shortcutRoutes.put("5,2", new LogicalPosition[]{
                new LogicalPosition(pieceId, 1, 20),
                new LogicalPosition(pieceId, 2, 20),
                new LogicalPosition(pieceId, 3, 10),
                new LogicalPosition(pieceId, 1, 50),
                new LogicalPosition(pieceId, 2, 50),
                new LogicalPosition(pieceId, 0, 1)
        });
        shortcutRoutes.put("5,3", new LogicalPosition[]{
                new LogicalPosition(pieceId, 2, 30),
                new LogicalPosition(pieceId, 1, 30),
                new LogicalPosition(pieceId, 3, 10),
                new LogicalPosition(pieceId, 1, 50),
                new LogicalPosition(pieceId, 2, 50),
                new LogicalPosition(pieceId, 0, 1)
        });

        // === 현재가 지름길 시작점이면 ===
        String key = a + "," + b;
        if (shortcutRoutes.containsKey(key)) {
            LogicalPosition[] route = shortcutRoutes.get(key);
            if (step - 1 < route.length) {
                return route[step - 1];
            } else {
                return route[route.length - 1]; // 마지막 지점 고정
            }
        }

        // === 지름길 도중 분기 처리 (예: (3,10) 이후) ===
        if (prevA == 5 && prevB == 1 && a == 3 && b == 10) {
            LogicalPosition[] tail = new LogicalPosition[]{
                    new LogicalPosition(pieceId, 1, 40),
                    new LogicalPosition(pieceId, 2, 40),
                    new LogicalPosition(pieceId, 5, 4),
                    new LogicalPosition(pieceId, 1, 5),
                    new LogicalPosition(pieceId, 2, 5),
                    new LogicalPosition(pieceId, 3, 5),
                    new LogicalPosition(pieceId, 4, 5),
                    new LogicalPosition(pieceId, 0, 1)
            };
            return step - 1 < tail.length ? tail[step - 1] : tail[tail.length - 1];
        }
        if (prevA == 5 && prevB == 2 && a == 3 && b == 10) {
            LogicalPosition[] tail = new LogicalPosition[]{
                    new LogicalPosition(pieceId, 1, 50),
                    new LogicalPosition(pieceId, 2, 50),
                    new LogicalPosition(pieceId, 0, 1)
            };
            return step - 1 < tail.length ? tail[step - 1] : tail[tail.length - 1];
        }
        if (prevA == 5 && prevB == 3 && a == 3 && b == 10) {
            LogicalPosition[] tail = new LogicalPosition[]{
                    new LogicalPosition(pieceId, 1, 50),
                    new LogicalPosition(pieceId, 2, 50),
                    new LogicalPosition(pieceId, 0, 1)
            };
            return step - 1 < tail.length ? tail[step - 1] : tail[tail.length - 1];
        }

        // === 기본 루트 ===
        LogicalPosition[] normalRoute = new LogicalPosition[]{
                new LogicalPosition(pieceId, 0, 1), new LogicalPosition(pieceId, 1, 1), new LogicalPosition(pieceId, 2, 1),
                new LogicalPosition(pieceId, 3, 1), new LogicalPosition(pieceId, 4, 1), new LogicalPosition(pieceId, 5, 1),
                new LogicalPosition(pieceId, 1, 2), new LogicalPosition(pieceId, 2, 2), new LogicalPosition(pieceId, 3, 2),
                new LogicalPosition(pieceId, 4, 2), new LogicalPosition(pieceId, 5, 2), new LogicalPosition(pieceId, 1, 3),
                new LogicalPosition(pieceId, 2, 3), new LogicalPosition(pieceId, 3, 3), new LogicalPosition(pieceId, 4, 3),
                new LogicalPosition(pieceId, 5, 3), new LogicalPosition(pieceId, 1, 4), new LogicalPosition(pieceId, 2, 4),
                new LogicalPosition(pieceId, 3, 4), new LogicalPosition(pieceId, 4, 4), new LogicalPosition(pieceId, 5, 4),
                new LogicalPosition(pieceId, 1, 5), new LogicalPosition(pieceId, 2, 5), new LogicalPosition(pieceId, 3, 5),
                new LogicalPosition(pieceId, 4, 5), new LogicalPosition(pieceId, 0, 1)
        };

        for (int i = 0; i < normalRoute.length; i++) {
            if (normalRoute[i].getA() == a && normalRoute[i].getB() == b) {
                int destIndex = Math.min(i + step, normalRoute.length - 1);
                return normalRoute[destIndex];
            }
        }

        return new LogicalPosition(pieceId, a, b); // fallback
    }

    private static int yutResultToStep(YutResult result) {
        return switch (result) {
            case DO -> 1;
            case GAE -> 2;
            case GEOL -> 3;
            case YUT -> 4;
            case MO -> 5;
            case BACK_DO -> -1;
        };
    }

    private static LogicalPosition calculateHEXAGON(
            Long pieceId,
            int a,
            int b,
            int prevA,
            int prevB,
            YutResult result) {
        return new LogicalPosition(pieceId, 5, 8);
    }


}
