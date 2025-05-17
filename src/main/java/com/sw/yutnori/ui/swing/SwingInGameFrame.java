package com.sw.yutnori.ui.swing;

import com.sw.yutnori.ui.display.GameSetupDisplay;
import com.sw.yutnori.ui.swing.panel.SwingStatusPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutBoardPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutControlPanel;
import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwingInGameFrame extends JFrame {
    private final SwingYutBoardPanel yutBoardPanel;
    private final SwingYutControlPanel controlPanel;
    private final SwingStatusPanel statusPanel;

    public SwingInGameFrame(InGameController controller) {
        setTitle("윷놀이 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setSize(1500, 1000);

        int boardPanelWidth = 1000;
        int boardPanelHeight = 800;
        int controlPanelWidth = 350;
        int controlPanelHeight = 900;
        int statusPanelHeight = 80;

        this.yutBoardPanel = controller.getYutBoardPanel();
        this.controlPanel = controller.getControlPanel();
        this.statusPanel = controller.getStatusPanel();

        yutBoardPanel.setPreferredSize(new Dimension(boardPanelWidth, boardPanelHeight));
        controlPanel.setPreferredSize(new Dimension(controlPanelWidth, controlPanelHeight));
        statusPanel.setPreferredSize(new Dimension(1500, statusPanelHeight));

        add(yutBoardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        int frameWidth = 1600;
        int frameHeight = 1100;
        int controlPanelWidth = 350;
        int statusPanelHeight = 100;
        int boardPanelWidth = frameWidth - controlPanelWidth;
        int boardPanelHeight = frameHeight - statusPanelHeight;

        // 예시 SetupData
        List<GameSetupDisplay.PlayerInfo> players = List.of(
                new GameSetupDisplay.PlayerInfo("플레이어1", "RED"),
                new GameSetupDisplay.PlayerInfo("플레이어2", "BLUE")
        );
        GameSetupDisplay.SetupData setupData = new GameSetupDisplay.SetupData("오각형", 2, 4, players);

        // 모델 및 매니저 생성
        Board model = new Board("pentagon", boardPanelWidth, boardPanelHeight);
        GameManager gameManager = new GameManager();
        gameManager.createGameFromSetupData(setupData);

        InGameController controller = new InGameController(model, gameManager, setupData);

        List<Player> playerList = gameManager.getCurrentGame().getPlayers();
        if (!playerList.isEmpty()) {
            Long firstPlayerId = playerList.get(0).getId();
            controller.setGameContext(firstPlayerId);
            controller.getYutBoardPanel().renderPieceObjects(firstPlayerId, playerList.get(0).getPieces());
        }

        SwingUtilities.invokeLater(() -> new SwingInGameFrame(controller));
    }
}
