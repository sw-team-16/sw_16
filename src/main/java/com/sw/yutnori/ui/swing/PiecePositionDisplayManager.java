package com.sw.yutnori.ui.swing;

import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.logic.util.ColorUtils;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.model.LogicalPosition;
import com.sw.yutnori.model.Node;
import com.sw.yutnori.model.Piece;

import javax.swing.*;
import java.awt.*;

public class PiecePositionDisplayManager {

    private final Board board;
    private final JPanel boardPanel;
    private final GameManager gameManager;

    public PiecePositionDisplayManager(Board board, JPanel boardPanel, GameManager gameManager) {
        this.board = board;
        this.boardPanel = boardPanel;
        this.gameManager = gameManager;
    }


    public void showLogicalPosition(LogicalPosition position, Long pieceId) {
        System.out.println("[디버깅] showLogicalPosition() 호출됨: pieceId = " + pieceId);
        Node node = board.findNode(position.getA(), position.getB());
        if (node == null) return;

        Piece piece = gameManager.getPiece(pieceId);
        if (piece == null || piece.getPlayer() == null) return;

        String colorName = piece.getPlayer().getColor();
        System.out.println("[디버깅] 플레이어의 색상: " + colorName);
        Color color = ColorUtils.parseColor(colorName); // 유틸리티 사용
        System.out.printf("[디버깅] marker 추가 위치: (%d, %d), pieceId=%d, 색상=%s%n",
                node.getX(), node.getY(), pieceId, color);

        JButton marker = new JButton(String.valueOf(pieceId));
        marker.setBounds((int) node.getX() - 15, (int) node.getY() - 15, 30, 30);
        marker.setBackground(color);
        marker.setOpaque(true);
        marker.setBorderPainted(false);
        boardPanel.add(marker);
        boardPanel.revalidate(); // 추가 : ui 상에서 말이 갑자기 사라짐
        boardPanel.repaint();
    }


}
