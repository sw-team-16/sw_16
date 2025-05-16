/*
 * SwingStatusPanel.java
 * 윷놀이 게임 상태 패널 클래스
 *  - 플레이어 상태 표시
 *  - 플레이어 당 말 개수 표시
 * 
 * 
 */
package com.sw.yutnori.ui.swing.panel;

import javax.swing.*;
import java.awt.*;

import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.Player;
import com.sw.yutnori.model.enums.PieceState;
import com.sw.yutnori.ui.display.GameSetupDisplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 임시 제작
public class SwingStatusPanel extends JPanel {
    private final Map<String, JPanel> playerPanels = new HashMap<>();
    private final GameManager gameManager;

    public SwingStatusPanel(List<GameSetupDisplay.PlayerInfo> players, int pieceCount, GameManager gameManager) {
        this.gameManager = gameManager;
        setLayout(new GridLayout(1, players.size()));
        setBorder(BorderFactory.createTitledBorder("Status"));
        for (GameSetupDisplay.PlayerInfo player : players) {
            add(createPlayerPanel(player, pieceCount));
        }
    }

    private JPanel createPlayerPanel(GameSetupDisplay.PlayerInfo player, int pieceCount) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder(player.name()));
        Color color = parseColor(player.color());
        for (int i = 0; i < pieceCount; i++) {
            JLabel piece = new JLabel();
            piece.setPreferredSize(new Dimension(20, 20));
            piece.setOpaque(true);
            piece.setBackground(color);
            panel.add(piece);
        }
        playerPanels.put(player.name(), panel);
        return panel;
    }

    private Color parseColor(String colorStr) {
        switch (colorStr.toUpperCase()) {
            case "RED": return new Color(255, 182, 193);
            case "BLUE": return new Color(173, 216, 230);
            case "GREEN": return new Color(144, 238, 144);
            case "YELLOW": return new Color(255, 255, 224);
            case "ORANGE": return new Color(255, 228, 181);
            case "PURPLE": return new Color(216, 191, 216);
            case "BLACK": return new Color(169, 169, 169);
            case "WHITE": return new Color(245, 245, 245);
            default: return new Color(211, 211, 211);
        }
    }
    public void updateCurrentPlayer(String currentPlayerName) {
        for (Map.Entry<String, JPanel> entry : playerPanels.entrySet()) {
            String playerName = entry.getKey();
            JPanel panel = entry.getValue();

            if (playerName.equals(currentPlayerName)) {
                panel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.RED, 2),
                        "▶ " + playerName
                ));
            } else {
                panel.setBorder(BorderFactory.createTitledBorder(playerName));
            }
        }

        revalidate();
        repaint();
    }

    // 플레이어 상태 (Status) 업데이트
    public void updatePlayerStatus(Player player) {
        JPanel panel = playerPanels.get(player.getName());
        if (panel == null) return;
        panel.removeAll();
        Color color = parseColor(player.getColor());
        // READY && !isFinished 말만 필터링
        List<Piece> readyPieces = player.getPieces().stream()
                .filter(p -> p.getState() == PieceState.READY && !p.isFinished())
                .toList();
        for (int i = 0; i < readyPieces.size(); i++) {
            JLabel piece = new JLabel();
            piece.setPreferredSize(new Dimension(20, 20));
            piece.setOpaque(true);
            piece.setBackground(color);
            panel.add(piece);
        }
        panel.revalidate();
        panel.repaint();
    }
}
