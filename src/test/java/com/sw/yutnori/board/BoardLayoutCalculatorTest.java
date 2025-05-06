package com.sw.yutnori.board;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardLayoutCalculatorTest {
    @Test
    void testCreateNodesSquare() {
        List<Node> nodes = BoardLayoutCalculator.createNodes("square", 400, 400);
        assertNotNull(nodes);
        assertTrue(nodes.size() > 0);
        System.out.println("Square board nodes: " + nodes.size());
        nodes.forEach(node -> System.out.println(node));
    }

    @Test
    void testCreateNodesPentagon() {
        List<Node> nodes = BoardLayoutCalculator.createNodes("pentagon", 400, 400);
        assertNotNull(nodes);
        assertTrue(nodes.size() > 0);
        System.out.println("Pentagon board nodes: " + nodes.size());
        nodes.forEach(node -> System.out.println(node));
    }

    @Test
    void testCreateNodesHexagon() {
        List<Node> nodes = BoardLayoutCalculator.createNodes("hexagon", 400, 400);
        assertNotNull(nodes);
        assertTrue(nodes.size() > 0);
        System.out.println("Hexagon board nodes: " + nodes.size());
        nodes.forEach(node -> System.out.println(node));
    }
}
