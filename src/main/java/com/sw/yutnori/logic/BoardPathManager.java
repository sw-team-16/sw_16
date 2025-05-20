package com.sw.yutnori.logic;

import com.sw.yutnori.model.LogicalPosition;
import com.sw.yutnori.model.enums.BoardType;
import com.sw.yutnori.model.enums.YutResult;

import java.util.*;

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
            YutResult result
    ) {
        int step = result.getStepCount();
        List<LogicalPosition> path;

        String key = a + "," + b;

        // 모든 경로 정의
        Map<String, List<LogicalPosition>> pathMap = new HashMap<>();

        pathMap.put("5,1", List.of(
                new LogicalPosition(pieceId, 5, 1),
                new LogicalPosition(pieceId, 10, 1),
                new LogicalPosition(pieceId, 10, 2),
                new LogicalPosition(pieceId, 3, 10),
                new LogicalPosition(pieceId, 40, 2),
                new LogicalPosition(pieceId, 40, 1),
                new LogicalPosition(pieceId, 0, 1)
        ));

        pathMap.put("10,1", pathMap.get("5,1").subList(1, pathMap.get("5,1").size()));
        pathMap.put("10,2", pathMap.get("5,1").subList(2, pathMap.get("5,1").size()));
        pathMap.put("3,10", List.of(
                new LogicalPosition(pieceId, 3, 10),
                new LogicalPosition(pieceId, 40, 2),
                new LogicalPosition(pieceId, 40, 1),
                new LogicalPosition(pieceId, 0, 1)
        ));
        pathMap.put("40,2", pathMap.get("3,10").subList(1, pathMap.get("3,10").size()));
        pathMap.put("40,1", pathMap.get("3,10").subList(2, pathMap.get("3,10").size()));

        pathMap.put("5,2", List.of(
                new LogicalPosition(pieceId, 5, 2),
                new LogicalPosition(pieceId, 20, 1),
                new LogicalPosition(pieceId, 20, 2),
                new LogicalPosition(pieceId, 3, 10),
                new LogicalPosition(pieceId, 40, 2),
                new LogicalPosition(pieceId, 40, 1),
                new LogicalPosition(pieceId, 0, 1)
        ));
        pathMap.put("20,1", pathMap.get("5,2").subList(1, pathMap.get("5,2").size()));
        pathMap.put("20,2", pathMap.get("5,2").subList(2, pathMap.get("5,2").size()));

        pathMap.put("5,3", List.of(
                new LogicalPosition(pieceId, 5, 3),
                new LogicalPosition(pieceId, 1, 4),
                new LogicalPosition(pieceId, 2, 4),
                new LogicalPosition(pieceId, 3, 4),
                new LogicalPosition(pieceId, 4, 4),
                new LogicalPosition(pieceId, 0, 1)
        ));
        pathMap.put("1,4", pathMap.get("5,3").subList(1, pathMap.get("5,3").size()));
        pathMap.put("2,4", pathMap.get("5,3").subList(2, pathMap.get("5,3").size()));
        pathMap.put("3,4", pathMap.get("5,3").subList(3, pathMap.get("5,3").size()));
        pathMap.put("4,4", pathMap.get("5,3").subList(4, pathMap.get("5,3").size()));

        // 일반 경로
        List<LogicalPosition> generalPath = List.of(
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

        if (pathMap.containsKey(key)) {
            path = pathMap.get(key);
        } else {
            path = generalPath;
        }

        // 현재 위치 인덱스 탐색
        int currentIdx = -1;
        for (int i = 0; i < path.size(); i++) {
            LogicalPosition pos = path.get(i);
            if (pos.getA() == a && pos.getB() == b) {
                currentIdx = i;
                break;
            }
        }

        if (currentIdx == -1) return new LogicalPosition(pieceId, a, b);

        int nextIdx = Math.min(currentIdx + step, path.size() - 1);
        return path.get(nextIdx);
    }
    public static LogicalPosition calculatePENTAGON(
            Long pieceId,
            int a,
            int b,
            int prevA,
            int prevB,
            YutResult result
    ) {
        int step = yutResultToStep(result);
        List<LogicalPosition> path;
        String key = a + "," + b;

        Map<String, List<LogicalPosition>> pathMap = new HashMap<>();

        // 분기점 경로: (5,1)
        pathMap.put("5,1", List.of(
                new LogicalPosition(pieceId, 5, 1),
                new LogicalPosition(pieceId, 10, 1),
                new LogicalPosition(pieceId, 10, 2),
                new LogicalPosition(pieceId, 3, 10),
                new LogicalPosition(pieceId, 40, 2),
                new LogicalPosition(pieceId, 40, 1),
                new LogicalPosition(pieceId, 5, 4),
                new LogicalPosition(pieceId, 1, 5),
                new LogicalPosition(pieceId, 2, 5),
                new LogicalPosition(pieceId, 3, 5),
                new LogicalPosition(pieceId, 4, 5),
                new LogicalPosition(pieceId, 0, 1)
        ));
        pathMap.put("10,1", pathMap.get("5,1").subList(1, pathMap.get("5,1").size()));
        pathMap.put("10,2", pathMap.get("5,1").subList(2, pathMap.get("5,1").size()));
        pathMap.put("3,10", pathMap.get("5,1").subList(3, pathMap.get("5,1").size()));
        pathMap.put("40,2", pathMap.get("5,1").subList(4, pathMap.get("5,1").size()));
        pathMap.put("40,1", pathMap.get("5,1").subList(5, pathMap.get("5,1").size()));
        pathMap.put("5,4", pathMap.get("5,1").subList(6, pathMap.get("5,1").size()));
        pathMap.put("1,5", pathMap.get("5,1").subList(7, pathMap.get("5,1").size()));
        pathMap.put("2,5", pathMap.get("5,1").subList(8, pathMap.get("5,1").size()));
        pathMap.put("3,5", pathMap.get("5,1").subList(9, pathMap.get("5,1").size()));
        pathMap.put("4,5", pathMap.get("5,1").subList(10, pathMap.get("5,1").size()));

        // 분기점 경로: (5,2)
        pathMap.put("5,2", List.of(
                new LogicalPosition(pieceId, 5, 2),
                new LogicalPosition(pieceId, 20, 1),
                new LogicalPosition(pieceId, 20, 2),
                new LogicalPosition(pieceId, 3, 10),
                new LogicalPosition(pieceId, 40, 2),
                new LogicalPosition(pieceId, 40, 1),
                new LogicalPosition(pieceId, 5, 4),
                new LogicalPosition(pieceId, 1, 5),
                new LogicalPosition(pieceId, 2, 5),
                new LogicalPosition(pieceId, 3, 5),
                new LogicalPosition(pieceId, 4, 5),
                new LogicalPosition(pieceId, 0, 1)
        ));
        pathMap.put("20,1", pathMap.get("5,2").subList(1, pathMap.get("5,2").size()));
        pathMap.put("20,2", pathMap.get("5,2").subList(2, pathMap.get("5,2").size()));

        // 분기점 경로: (5,3)
        pathMap.put("5,3", List.of(
                new LogicalPosition(pieceId, 5, 3),
                new LogicalPosition(pieceId, 30, 1),
                new LogicalPosition(pieceId, 30, 2),
                new LogicalPosition(pieceId, 3, 10),
                new LogicalPosition(pieceId, 50, 2),
                new LogicalPosition(pieceId, 50, 1),
                new LogicalPosition(pieceId, 0, 1)
        ));
        pathMap.put("30,1", pathMap.get("5,3").subList(1, pathMap.get("5,3").size()));
        pathMap.put("30,2", pathMap.get("5,3").subList(2, pathMap.get("5,3").size()));
        pathMap.put("50,2", pathMap.get("5,3").subList(4, pathMap.get("5,3").size()));
        pathMap.put("50,1", pathMap.get("5,3").subList(5, pathMap.get("5,3").size()));

        // 일반 경로
        List<LogicalPosition> generalPath = List.of(
                new LogicalPosition(pieceId, 0, 1),
                new LogicalPosition(pieceId, 1, 1), new LogicalPosition(pieceId, 2, 1),
                new LogicalPosition(pieceId, 3, 1), new LogicalPosition(pieceId, 4, 1), new LogicalPosition(pieceId, 5, 1),
                new LogicalPosition(pieceId, 1, 2), new LogicalPosition(pieceId, 2, 2), new LogicalPosition(pieceId, 3, 2),
                new LogicalPosition(pieceId, 4, 2), new LogicalPosition(pieceId, 5, 2), new LogicalPosition(pieceId, 1, 3),
                new LogicalPosition(pieceId, 2, 3), new LogicalPosition(pieceId, 3, 3), new LogicalPosition(pieceId, 4, 3),
                new LogicalPosition(pieceId, 5, 3), new LogicalPosition(pieceId, 1, 4), new LogicalPosition(pieceId, 2, 4),
                new LogicalPosition(pieceId, 3, 4), new LogicalPosition(pieceId, 4, 4), new LogicalPosition(pieceId, 5, 4),
                new LogicalPosition(pieceId, 1, 5), new LogicalPosition(pieceId, 2, 5), new LogicalPosition(pieceId, 3, 5),
                new LogicalPosition(pieceId, 4, 5), new LogicalPosition(pieceId, 0, 1)
        );

        path = pathMap.getOrDefault(key, generalPath);

        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getA() == a && path.get(i).getB() == b) {
                int destIndex = Math.min(i + step, path.size() - 1);
                return path.get(destIndex);
            }
        }

        return new LogicalPosition(pieceId, a, b);
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

        int step = yutResultToStep(result);

        // 지름길 정의
        Map<String, LogicalPosition[]> shortcuts = new HashMap<>();
        shortcuts.put("5,1", new LogicalPosition[]{
                new LogicalPosition(pieceId, 1, 10), new LogicalPosition(pieceId, 2, 10),
                new LogicalPosition(pieceId, 3, 10), new LogicalPosition(pieceId, 2, 40),
                new LogicalPosition(pieceId, 1, 40), new LogicalPosition(pieceId, 5, 4),
                new LogicalPosition(pieceId, 1, 5), new LogicalPosition(pieceId, 2, 5),
                new LogicalPosition(pieceId, 3, 5), new LogicalPosition(pieceId, 4, 5),
                new LogicalPosition(pieceId, 5, 5), new LogicalPosition(pieceId, 1, 6),
                new LogicalPosition(pieceId, 2, 6), new LogicalPosition(pieceId, 3, 6),
                new LogicalPosition(pieceId, 4, 6), new LogicalPosition(pieceId, 5, 6),
                new LogicalPosition(pieceId, 0, 1)
        });

        shortcuts.put("5,2", new LogicalPosition[]{
                new LogicalPosition(pieceId, 20, 1), new LogicalPosition(pieceId, 20, 2),
                new LogicalPosition(pieceId, 3, 10), new LogicalPosition(pieceId, 50, 2),
                new LogicalPosition(pieceId, 50, 1), new LogicalPosition(pieceId, 5, 5),
                new LogicalPosition(pieceId, 1, 6), new LogicalPosition(pieceId, 2, 6),
                new LogicalPosition(pieceId, 3, 6), new LogicalPosition(pieceId, 4, 6),
                new LogicalPosition(pieceId, 5, 6), new LogicalPosition(pieceId, 0, 1)
        });

        shortcuts.put("5,3", new LogicalPosition[]{
                new LogicalPosition(pieceId, 30, 1), new LogicalPosition(pieceId, 30, 2),
                new LogicalPosition(pieceId, 3, 10), new LogicalPosition(pieceId, 60, 2),
                new LogicalPosition(pieceId, 60, 1), new LogicalPosition(pieceId, 0, 1)
        });

        shortcuts.put("5,4", new LogicalPosition[]{
                new LogicalPosition(pieceId, 40, 1), new LogicalPosition(pieceId, 40, 2),
                new LogicalPosition(pieceId, 3, 10), new LogicalPosition(pieceId, 60, 2),
                new LogicalPosition(pieceId, 60, 1), new LogicalPosition(pieceId, 0, 1)
        });

        // (3,10)에 도착한 경우: 이전 위치에 따라 별도 분기
        if (a == 3 && b == 10) {
            if ((prevA == 5 && prevB == 1) || (prevA == 5 && prevB == 2) || (prevA == 5 && prevB == 4)) {
                LogicalPosition[] tail = {
                        new LogicalPosition(pieceId, 60, 2), new LogicalPosition(pieceId, 60, 1),
                        new LogicalPosition(pieceId, 0, 1)
                };
                return step - 1 < tail.length ? tail[step - 1] : tail[tail.length - 1];
            } else if (prevA == 5 && prevB == 3) {
                // 5,3 루트는 그대로 유지
                String key = prevA + "," + prevB;
                LogicalPosition[] route = shortcuts.getOrDefault(key, new LogicalPosition[0]);
                return step - 1 < route.length ? route[step - 1] : route[route.length - 1];
            }
        }

        // 지름길 분기점 처리
        String key = a + "," + b;
        if (shortcuts.containsKey(key)) {
            LogicalPosition[] route = shortcuts.get(key);
            return step - 1 < route.length ? route[step - 1] : route[route.length - 1];
        }

        // 기본 경로
        LogicalPosition[] normalRoute = {
                new LogicalPosition(pieceId, 0, 1), new LogicalPosition(pieceId, 1, 1),
                new LogicalPosition(pieceId, 2, 1), new LogicalPosition(pieceId, 3, 1),
                new LogicalPosition(pieceId, 4, 1), new LogicalPosition(pieceId, 5, 1),
                new LogicalPosition(pieceId, 1, 2), new LogicalPosition(pieceId, 2, 2),
                new LogicalPosition(pieceId, 3, 2), new LogicalPosition(pieceId, 4, 2),
                new LogicalPosition(pieceId, 5, 2), new LogicalPosition(pieceId, 1, 3),
                new LogicalPosition(pieceId, 2, 3), new LogicalPosition(pieceId, 3, 3),
                new LogicalPosition(pieceId, 4, 3), new LogicalPosition(pieceId, 5, 3),
                new LogicalPosition(pieceId, 1, 4), new LogicalPosition(pieceId, 2, 4),
                new LogicalPosition(pieceId, 3, 4), new LogicalPosition(pieceId, 4, 4),
                new LogicalPosition(pieceId, 5, 4), new LogicalPosition(pieceId, 1, 5),
                new LogicalPosition(pieceId, 2, 5), new LogicalPosition(pieceId, 3, 5),
                new LogicalPosition(pieceId, 4, 5), new LogicalPosition(pieceId, 5, 5),
                new LogicalPosition(pieceId, 1, 6), new LogicalPosition(pieceId, 2, 6),
                new LogicalPosition(pieceId, 3, 6), new LogicalPosition(pieceId, 4, 6),
                new LogicalPosition(pieceId, 5, 6), new LogicalPosition(pieceId, 0, 1)
        };

        for (int i = 0; i < normalRoute.length; i++) {
            if (normalRoute[i].getA() == a && normalRoute[i].getB() == b) {
                int dest = Math.min(i + step, normalRoute.length - 1);
                return normalRoute[dest];
            }
        }

        // 위치를 찾을 수 없을 경우 현재 위치 반환
        return new LogicalPosition(pieceId, a, b);
    }




}
