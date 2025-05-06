package com.sw.yutnori.ui;

import com.sw.yutnori.board.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class YutBoardPanel extends JPanel {
    private String boardType;
    private BoardGraph board;
    private Set<String> piecePositions = new HashSet<>();

    public YutBoardPanel(BoardGraph board) {
        this.board = board;
        this.boardType = board.getType();
        setBackground(Color.WHITE);
    }

    public void setPiecePositions(Collection<String> nodeIds) {
        this.piecePositions = new HashSet<>(nodeIds);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        this.board = new BoardGraph(boardType, w, h);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Node node : board.getAllNodes()) {
            for (Node conn : node.getConnections()) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine((int)node.getX(), (int)node.getY(), (int)conn.getX(), (int)conn.getY());
            }
        }

        for (Node node : board.getAllNodes()) {
            int x = (int) node.getX();
            int y = (int) node.getY();
            int r = 18;
            g2.setColor(node.getTypes().stream().anyMatch(t -> t.name().equals("POINT")) ? Color.RED : Color.GRAY);
            g2.fillOval(x - r/2, y - r/2, r, r);
            g2.setColor(Color.BLACK);
            g2.drawOval(x - r/2, y - r/2, r, r);
            g2.drawString(node.getId(), x - r/2, y - r/2 - 2);
        }
        for (String nodeId : piecePositions) {
            Node node = board.getNode(nodeId);
            if (node != null) {
                int x = (int) node.getX();
                int y = (int) node.getY();
                g2.setColor(Color.RED);
                g2.fillOval(x - 7, y - 7, 14, 14);
                g2.setColor(Color.BLACK);
                g2.drawOval(x - 7, y - 7, 14, 14);
            }
        }
    }
}
