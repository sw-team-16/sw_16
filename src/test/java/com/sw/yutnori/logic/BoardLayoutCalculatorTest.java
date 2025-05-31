package com.sw.yutnori.logic;

import com.sw.yutnori.model.Node;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardLayoutCalculatorTest {

    @Test
    void testCreateNodes_squareBoard_returnsExpectedNodeCountAndConnections() {
        List<Node> nodes = BoardLayoutCalculator.createNodes("square", 800, 800);
        assertNotNull(nodes);
        assertTrue(nodes.size() >= 20); // 기본적으로 4변 * 4 + center + diagonals + corners

        // 중앙 노드가 포함되어 있는지 확인
        assertTrue(nodes.stream().anyMatch(n -> n.getA() == 3 && n.getB() == 10));

        // 꼭짓점 중 하나 확인 (0,1)
        assertTrue(nodes.stream().anyMatch(n -> n.getA() == 0 && n.getB() == 1));
    }

    @Test
    void testCreateNodes_pentagonBoard_returnsNodes() {
        List<Node> nodes = BoardLayoutCalculator.createNodes("pentagon", 800, 800);
        assertNotNull(nodes);
        assertTrue(nodes.size() >= 30);

        // 중앙 노드와 꼭짓점 노드 포함 확인
        assertTrue(nodes.stream().anyMatch(n -> n.getA() == 3 && n.getB() == 10));
        assertTrue(nodes.stream().anyMatch(n -> n.getA() == 0 && n.getB() == 1));
    }

    @Test
    void testCreateNodes_hexagonBoard_returnsNodes() {
        List<Node> nodes = BoardLayoutCalculator.createNodes("hexagon", 800, 800);
        assertNotNull(nodes);
        assertTrue(nodes.size() >= 35);

        // 중심 노드가 있고, 지름길 노드도 존재하는지 확인
        assertTrue(nodes.stream().anyMatch(n -> n.getA() == 3 && n.getB() == 10));
        assertTrue(nodes.stream().anyMatch(n -> n.getA() == 0 && n.getB() == 1));
    }

    @Test
    void testCreateNodes_invalidType_defaultsToSquare() {
        List<Node> nodes = BoardLayoutCalculator.createNodes("unknownShape", 800, 800);
        assertNotNull(nodes);
        assertTrue(nodes.stream().anyMatch(n -> n.getA() == 3 && n.getB() == 10)); // center node
    }

    @Test
    void testNodeConnections_bidirectionalAndNonEmpty() {
        List<Node> nodes = BoardLayoutCalculator.createNodes("square", 800, 800);

        for (Node node : nodes) {
            for (Node connected : node.getConnections()) {
                assertTrue(connected.getConnections().contains(node),
                        "Connection should be bidirectional between nodes.");
            }
        }
    }

    @Test
    void testCreateNodes_nodeCountIsExactForEachBoardType() {
        // 중앙 노드 제외 노드 개수
        assertEquals(29, BoardLayoutCalculator.createNodes("square", 800, 800).size(), "Square board node count");
        assertEquals(36, BoardLayoutCalculator.createNodes("pentagon", 800, 800).size(), "Pentagon board node count");
        assertEquals(43, BoardLayoutCalculator.createNodes("hexagon", 800, 800).size(), "Hexagon board node count");
    }

    @Test
    void testCreateNodes_centerNodeHasCorrectCoordinates() {
        // 중앙 노드 좌표 확인
        int width = 800, height = 800;
        List<Node> nodes = BoardLayoutCalculator.createNodes("square", width, height);
        Node center = nodes.stream().filter(n -> n.getA() == 3 && n.getB() == 10).findFirst().orElseThrow();
        assertEquals(width / 2.0, center.getX(), 1.0, "Center node X should be near width/2");
        assertEquals(height / 2.0, center.getY(), 1.0, "Center node Y should be near height/2");
    }

    @Test
    void testCreateNodes_diagonalNodesExistAndAreConnected() {
        // 대각선(지름길) 노드가 존재하고 중심 노드 또는 꼭짓점과 연결되어 있는지 확인
        List<Node> nodes = BoardLayoutCalculator.createNodes("square", 800, 800);
        Node center = nodes.stream().filter(n -> n.getA() == 3 && n.getB() == 10).findFirst().orElseThrow();
        // 대각선(지름길) 노드는 a=0,10,20,30 등, b=1,2 등으로 생성됨
        boolean foundDiagonal = false;
        for (int logicA : new int[]{40, 10, 20, 30}) {
            for (int b = 1; b <= 2; b++) {
                final int bb = b;
                Node diag = nodes.stream().filter(n -> n.getA() == logicA && n.getB() == bb).findFirst().orElse(null);
                if (diag != null) {
                    foundDiagonal = true;
                    // 중심 노드 또는 꼭짓점과 연결되어 있는지
                    boolean connectedToCenter = diag.getConnections().contains(center);
                    boolean connectedToCorner = diag.getConnections().stream().anyMatch(n -> n.getA() == 0 && n.getB() == 1 || (n.getA() == 5 && n.getB() >= 1 && n.getB() <= 4));
                    assertTrue(connectedToCenter || connectedToCorner, "Diagonal node should be connected to center or corner");
                }
            }
        }
        assertTrue(foundDiagonal, "At least one diagonal node should exist");
    }
}
