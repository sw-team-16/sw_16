/*
 * SwingInGameFrame.java
 * 윷놀이 게임 메인 화면을 구현하는 클래스
 *  - 윷판 (왼쪽/가운데)
 *  - 윷 컨트롤 패널 (오른쪽)
 *  - 플레이어 상태 패널 (하단)
 * 
 * 
 */
package com.sw.yutnori.ui;

import com.sw.yutnori.client.GameApiClient;
import com.sw.yutnori.client.YutnoriApiClient;
import com.sw.yutnori.board.BoardModel;
import com.sw.yutnori.controller.InGameController;

import javax.swing.*;
import java.awt.*;

public class SwingInGameFrame extends JFrame {
    private final YutBoardPanel yutBoardPanel;
    private final SwingControlPanel controlPanel;
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

        // Controller에서 패널을 받아서 추가
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
        // example: 직접 생성 (실제 사용에서는 Controller가 모든 의존성 관리)
        int frameWidth = 1600;
        int frameHeight = 1100;
        int controlPanelWidth = 350;
        int statusPanelHeight = 100;
        int boardPanelWidth = frameWidth - controlPanelWidth;
        int boardPanelHeight = frameHeight - statusPanelHeight;
        BoardModel model = new BoardModel("pentagon", boardPanelWidth, boardPanelHeight);
        GameApiClient apiClient = new YutnoriApiClient();
        // 임의의 플레이어 정보 생성
        java.util.List<com.sw.yutnori.ui.display.GameSetupDisplay.PlayerInfo> players = java.util.List.of(
            new com.sw.yutnori.ui.display.GameSetupDisplay.PlayerInfo("플레이어1", "#FF0000"),
            new com.sw.yutnori.ui.display.GameSetupDisplay.PlayerInfo("플레이어2", "#0000FF")
        );
        com.sw.yutnori.ui.display.GameSetupDisplay.SetupData setupData =
            new com.sw.yutnori.ui.display.GameSetupDisplay.SetupData("오각형", 2, 4, players);
        InGameController controller = new InGameController(model, apiClient, setupData);
        SwingUtilities.invokeLater(() -> new SwingInGameFrame(controller));
    }
}