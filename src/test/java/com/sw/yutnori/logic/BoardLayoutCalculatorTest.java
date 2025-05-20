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
}
