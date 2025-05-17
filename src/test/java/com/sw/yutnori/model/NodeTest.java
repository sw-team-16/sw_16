package com.sw.yutnori.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {
    // a, b, x, y 인자를 받아 초기화
    @Test
    void constructor_initializesFieldsCorrectly() {
        Node node = new Node(1, 2, 100.0, 150.0);
        assertEquals(1, node.getA());
        assertEquals(2, node.getB());
        assertEquals(100.0, node.getX());
        assertEquals(150.0, node.getY());
        assertNotNull(node.getConnections());
        assertTrue(node.getConnections().isEmpty());
    }

    // 노드 연결
    @Test
    void addConnection_addsNodeToConnectionsList() {
        Node node1 = new Node(1, 1, 0, 0);
        Node node2 = new Node(1, 2, 0, 0);
        node1.addConnection(node2);
        assertEquals(1, node1.getConnections().size());
        assertTrue(node1.getConnections().contains(node2));
    }

    // 여러 노드 연결
    @Test
    void addConnection_canAddMultipleNodes() {
        Node node1 = new Node(1, 1, 0, 0);
        Node node2 = new Node(1, 2, 0, 0);
        Node node3 = new Node(1, 3, 0, 0);
        node1.addConnection(node2);
        node1.addConnection(node3);
        assertEquals(2, node1.getConnections().size());
        assertTrue(node1.getConnections().contains(node2));
        assertTrue(node1.getConnections().contains(node3));
    }

    // 연결된 노드가 없는 경우
    @Test
    void getConnections_returnsEmptyListIfNoConnectionsAdded() {
        Node node = new Node(1, 1, 0, 0);
        assertNotNull(node.getConnections());
        assertTrue(node.getConnections().isEmpty());
    }

    // a, b가 같은 경우
    @Test
    void equals_sameAandBAreEqual() {
        Node n1 = new Node(1, 2, 10, 20);
        Node n2 = new Node(1, 2, 30, 40);
        assertEquals(n1, n2);
    }

    // a, b가 다른 경우
    @Test
    void equals_differentAorBAreNotEqual() {
        Node n1 = new Node(1, 2, 10, 20);
        Node n3 = new Node(2, 2, 10, 20);
        assertNotEquals(n1, n3);
    }

    // 자기 자신과 같은 경우
    @Test
    void equals_reflexivity() {
        Node n1 = new Node(1, 2, 10, 20);
        assertEquals(n1, n1);
    }

    // null과 비교
    @Test
    void equals_comparisonWithNull() {
        Node n1 = new Node(1, 2, 10, 20);
        assertNotEquals(n1, null);
    }

    // 다른 타입과 비교
    @Test
    void equals_comparisonWithDifferentObjectType() {
        Node n1 = new Node(1, 2, 10, 20);
        String s = "test";
        assertNotEquals(n1, s);
    }

    // toString
    @Test
    void toString_returnsFormattedString() {
        Node node = new Node(1, 2, 10.0, 20.0);
        String str = node.toString();
        assertNotNull(str);
        assertTrue(str.contains("Node(1, 2, x=10.0, y=20.0)"));
    }
} 