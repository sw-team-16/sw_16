package com.sw.yutnori.ui.swing;

import com.sw.yutnori.board.BoardModel;
import com.sw.yutnori.board.Node;
import com.sw.yutnori.common.LogicalPosition;
import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.util.ColorUtils;

import javax.swing.*;
import java.awt.*;

public class PiecePositionDisplayManager {
    private final BoardModel boardModel;
    private final JPanel boardPanel;
    private final GameManager gameManager;

    public PiecePositionDisplayManager(BoardModel boardModel, JPanel boardPanel, GameManager gameManager) {
        this.boardModel = boardModel;
        this.boardPanel = boardPanel;
        this.gameManager = gameManager;
    }


    public void showLogicalPosition(LogicalPosition position, Long pieceId) {
        Node node = boardModel.findNode(position.getA(), position.getB());
        if (node == null) return;

        Piece piece = gameManager.getPiece(pieceId);
        if (piece == null || piece.getPlayer() == null) return;

        String colorName = piece.getPlayer().getColor();
        System.out.println("[디버깅] 플레이어의 색상: " + colorName);

        Color color = ColorUtils.parseColor(colorName); // 유틸리티 사용

        JButton marker = new JButton(String.valueOf(pieceId));
        marker.setBounds((int) node.getX() - 15, (int) node.getY() - 15, 30, 30);
        marker.setBackground(color);
        marker.setOpaque(true);
        marker.setBorderPainted(false);
        boardPanel.add(marker);
        boardPanel.repaint();
    }


}
