package com.sw.yutnori.board;

import java.util.*;

public class Node {
    private final String id;
    private final double x, y;
    private final EnumSet<NodeType> types;
    private final List<Node> connections = new ArrayList<>();

    public Node(String id, double x, double y, NodeType... types) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.types = EnumSet.noneOf(NodeType.class);
        Collections.addAll(this.types, types);
    }
    public void addConnection(Node node) {
        if (!connections.contains(node)) {
            connections.add(node);
        }
    }
    public String getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean hasType(NodeType type) { return types.contains(type); }
    public EnumSet<NodeType> getTypes() { return EnumSet.copyOf(types); }
    public List<Node> getConnections() { return connections; }
}
