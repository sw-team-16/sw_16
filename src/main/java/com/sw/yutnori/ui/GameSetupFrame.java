package com.sw.yutnori.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class GameSetupFrame extends JFrame {
    private GameSetupPanel setupPanel;
    // !TODO: Connect with backend api end point
    private static final String API_URL = "";

    public GameSetupFrame() {
        setTitle("윷놀이 게임 설정");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setupPanel = new GameSetupPanel();
        add(setupPanel, BorderLayout.CENTER);

        // 게임 시작 버튼 클릭 시 API 호출 (`api/game`)
        // !TODO: 아직 api endpoint가 정의되지 않음
        setupPanel.getStartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String boardTypeKor = setupPanel.getBoardType();
                String boardType = "SQUARE";
                if ("오각형".equals(boardTypeKor)) boardType = "PENTAGON";
                else if ("육각형".equals(boardTypeKor)) boardType = "HEXAGON";
                // JSON body 생성
                String json = String.format("{\"boardType\":\"%s\",\"numPlayers\":%d,\"numPieces\":%d}",
                        boardType, setupPanel.getPlayerCount(), setupPanel.getPieceCount());
                try {
                    URL url = new URI(API_URL + "/api/game").toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(json.getBytes());
                    }
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        JOptionPane.showMessageDialog(GameSetupFrame.this, "게임 설정이 서버에 전송되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(GameSetupFrame.this, "서버 오류:\n" + responseCode, "오류", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GameSetupFrame.this, "API 요청 실패:\n" + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public GameSetupPanel getSetupPanel() {
        return setupPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameSetupFrame frame = new GameSetupFrame();
            frame.setVisible(true);
        });
    }
}
