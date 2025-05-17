/*
 * Node.java
 * 윷판을 구성하는 하나의 노드
 * 
 * 
 */
package com.sw.yutnori.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    private final int a;
    private final int b;
    private final double x;
    private final double y;
    private final List<Node> connections = new ArrayList<>();

    public Node(int a, int b, double x, double y) {
        /*
         * a, b: 논리 좌표
         * a: b번째 변의 a번째 노드
         * b: b번째 변
         * e.g. (1, 2) -> 2번째 변의 1번째 노드
         * x, y: 실제 좌표
         */
        this.a = a;
        this.b = b;
        this.x = x;
        this.y = y;
    }

    public int getA() { return a; }
    public int getB() { return b; }
    public double getX() { return x; }
    public double getY() { return y; }

    public void addConnection(Node node) {
        connections.add(node);
    }

    public List<Node> getConnections() { return connections; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return a == node.a && b == node.b;
    }

    @Override
    public String toString() {
        return "Node(" + a + ", " + b + ", x=" + x + ", y=" + y + ")";
    }
}
