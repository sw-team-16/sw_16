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

        JButton marker = new JButton("P" + pieceId);
        marker.setBounds((int) node.getX() - 15, (int) node.getY() - 15, 30, 30);
        marker.setBackground(Color.YELLOW);
        boardPanel.add(marker);
        boardPanel.repaint();
    }
}