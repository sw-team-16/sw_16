package com.sw.yutnori.ui;

import com.sw.yutnori.client.GameApiClient;
import com.sw.yutnori.client.TestYutnoriApiClient;
import com.sw.yutnori.client.YutnoriApiClient;

import javax.swing.*;
import java.awt.*;

public class SwingInGameFrame extends JFrame {
    public SwingInGameFrame() {
        setTitle("윷놀이 게임");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 테스트용 - new TestYutnoriApiClient() 대입 / API 연동 - new YutnoriApiClient() 대입
        GameApiClient apiClient = new YutnoriApiClient();

        // 컨트롤 패널에 API 클라이언트 전달
        SwingControlPanel controlPanel = new SwingControlPanel(apiClient);

        // 게임아이디 및 플레이어 아이디 알맞은 설정 필요
        controlPanel.setGameContext(1L, 1L);

//        add(new BoardPanel(), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        SwingStatusPanel statusPanel = new SwingStatusPanel();
        statusPanel.setPreferredSize(new Dimension(statusPanel.getPreferredSize().width, 100));
        add(statusPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // 화면 중앙에 표시
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingInGameFrame::new);
    }
}