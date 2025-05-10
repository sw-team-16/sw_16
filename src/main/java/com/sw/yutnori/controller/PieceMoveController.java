package com.sw.yutnori.controller;

import com.sw.yutnori.board.BoardModel;
import com.sw.yutnori.board.Node;
import com.sw.yutnori.common.enums.YutResult;

import java.util.List;
import java.util.function.Function;

public class PieceMoveController {

    private BoardModel boardModel;

    // 생성자: 게임판 모델(BoardModel)을 받아 관리하는 역할을 수행합니다.
    public void MoveManager(BoardModel boardModel) {
        this.boardModel = boardModel;
    }

    public PieceMoveController(BoardModel boardModel) {
        this.boardModel = boardModel;
    }

    /**
     * YutResult(윷놀이 결과: 도, 개, 걸 등)를 이동할 칸 수로 변환합니다.
     *
     * @param yutResult 윷놀이 결과 값 (DO, GAE, GEOL, BACK_DO 등)
     * @return 이동해야 할 칸 수 (빽도는 음수, 나머지는 양수)
     */
    public int yutResultToStep(YutResult yutResult) {
        switch (yutResult) {
            case DO:
                return 1;
            case GAE:
                return 2;
            case GEOL:
                return 3;
            case YUT:
                return 4;
            case MO:
                return 5;
            case BACK_DO:
                return -1;
            default:
                throw new IllegalArgumentException("알 수 없는 YutResult: " + yutResult);
        }
    }

    /**
     * 주어진 시작 노드에서 특정 칸 수만큼 앞으로 이동합니다.
     *
     * @param startNode 시작 노드 (현재 위치)
     * @param steps     앞으로 이동할 칸 수
     * @return 이동 후 도달한 노드 (더 이상 경로가 없으면 null 반환)
     */
    public Node moveForward(Node startNode, int steps) {
        Node currentNode = startNode;

        for (int i = 0; i < steps; i++) {
            if (currentNode.getConnections().isEmpty()) {
                // 현재 노드에서 연결된 경로가 없다면 이동을 멈춥니다.
                return null;
            }
            // 연결된 첫 번째 노드로 이동 (기본 경로 설정)
            currentNode = currentNode.getConnections().get(0);
        }

        return currentNode;
    }

    /**
     * 현재 노드 기준으로 한 칸 뒤로 이동합니다 (BACK_DO 동작).
     *
     * @param startNode 현재 위치한 노드
     * @return 이전 노드 (연결된 경로가 없을 시 null 반환)
     */
    public Node moveBackward(Node startNode) {
        // 게임판의 모든 노드를 검색하여 해당 노드로 연결된 노드를 찾음
        return boardModel.getNodes().stream()
                .filter(node -> node.getConnections().contains(startNode)) // startNode로 연결된 노드 찾기
                .findFirst()
                .orElse(null); // 이전 노드가 없으면 null 반환
    }

    /**
     * 주어진 노드가 여러 경로로 분기되는 지점을 나타내는지 확인합니다.
     *
     * @param node 확인할 노드
     * @return true: 분기점인 경우, false: 단일 경로인 경우
     */
    public boolean isBranchingPoint(Node node) {
        return node.getConnections().size() > 1;
    }

    /**
     * 분기점 노드에서 특정 규칙에 따라 다음 이동할 경로를 선택합니다.
     *
     * @param node        분기점 노드
     * @param pathChooser 분기점에서 이동할 경로를 결정하는 함수 (사용자 정의)
     * @return 선택된 경로의 노드
     */
    public Node chooseBranchPath(Node node, Function<List<Node>, Node> pathChooser) {
        if (!isBranchingPoint(node)) {
            throw new IllegalStateException("현재 노드는 분기점이 아닙니다: " + node);
        }

        // 사용자 정의 로직으로 경로 선택
        return pathChooser.apply(node.getConnections());
    }

    /**
     * 주어진 노드에서 지정된 칸 수만큼 이동합니다.
     * 칸 수가 양수이면 앞으로 이동하고 음수이면 뒤로 이동합니다.
     *
     * @param startNode 시작 노드
     * @param steps     이동할 칸 수 (양수: 앞으로, 음수: 뒤로)
     * @return 이동 후 도달한 노드
     */
    public Node traverse(Node startNode, int steps) {
        if (steps > 0) {
            return moveForward(startNode, steps); // 양수: 앞으로 이동
        } else if (steps < 0) {
            return moveBackward(startNode); // 음수: 뒤로 이동
        }
        return startNode; // steps가 0이면 현재 위치 유지
    }

    /**
     * 윷 결과에 따라 다음 이동할 노드를 계산합니다.
     *
     * @param startNode 시작 노드 (현재 위치한 노드)
     * @param yutResult 윷 던지기 결과 (DO, GAE, BACK_DO 등)
     * @return 이동 후 도달한 노드
     */
    public Node calculateNextNode(Node startNode, YutResult yutResult) {
        // YutResult를 이동할 칸 수로 변환
        int steps = yutResultToStep(yutResult);
        // 변환한 칸 수만큼 이동
        return traverse(startNode, steps);
    }
}