package com.sw.yutnori.board;

import java.util.*;

public class BoardGraph {
    private final List<Node> nodes;
    private final Map<String, Node> nodeMap;
    private final String boardType;

    public BoardGraph(String boardType, int width, int height) {
        this.boardType = boardType;
        this.nodes = BoardLayoutCalculator.createNodes(boardType, width, height);
        this.nodeMap = new HashMap<>();
        for (Node node : nodes) {
            nodeMap.put(node.getA() + "," + node.getB(), node);
        }
    }

    public Node getNode(String id) {
        return nodeMap.get(id);
    }

    public List<Node> getAllNodes() {
        return new ArrayList<>(nodes);
    }

    public List<Node> getMovableNodes(Node currentNode, int steps) {
        List<Node> result = new ArrayList<>();
        Queue<PathNode> queue = new LinkedList<>();
        queue.add(new PathNode(currentNode, 0));
        Set<Node> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            PathNode pathNode = queue.poll();
            Node node = pathNode.node;
            int currentSteps = pathNode.steps;
            if (currentSteps == steps) {
                result.add(node);
                continue;
            }
            if (currentSteps < steps) {
                for (Node next : node.getConnections()) {
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(new PathNode(next, currentSteps + 1));
                    }
                }
            }
        }
        return result;
    }

    public String getType() {
        return this.boardType;
    }

    private static class PathNode {
        Node node;
        int steps;
        PathNode(Node node, int steps) {
            this.node = node;
            this.steps = steps;
        }
    }
}
