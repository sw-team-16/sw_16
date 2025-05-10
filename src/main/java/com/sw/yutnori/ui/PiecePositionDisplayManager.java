package com.sw.yutnori.ui;

import com.sw.yutnori.board.BoardModel;
import com.sw.yutnori.board.Node;
import com.sw.yutnori.common.LogicalPosition;

import javax.swing.*;
import java.awt.*;

public class PiecePositionDisplayManager {
    private final BoardModel boardModel;
    private final JPanel boardPanel;

    public PiecePositionDisplayManager(BoardModel boardModel, JPanel boardPanel) {
        this.boardModel = boardModel;
        this.boardPanel = boardPanel;
    }

    public void showLogicalPosition(LogicalPosition position, Long pieceId) {
        Node node = boardModel.findNode(position.getA(), position.getB());
        if (node == null) return;

        int markerSize = 30;
        int markerX = (int) node.getX() - markerSize / 2;
        int markerY = (int) node.getY() - markerSize / 2;

        JButton marker = new JButton("P" + pieceId);
        marker.setBounds(markerX, markerY, markerSize, markerSize);
        marker.setBackground(Color.YELLOW);
        marker.setFocusPainted(false);
        marker.setBorderPainted(false);

        boardPanel.setComponentZOrder(marker, 0);
        boardPanel.add(marker);
        boardPanel.repaint();
    }
}
