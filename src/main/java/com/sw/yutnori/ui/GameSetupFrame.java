/*
 * GameSetupFrame.java
 * 게임 설정 화면을 구현하는 클래스
 * 
 * 
 * 
 */
package com.sw.yutnori.ui;

import com.sw.yutnori.ui.display.GameSetupDisplay;
import com.sw.yutnori.ui.display.SwingGameSetupDisplay;
import com.sw.yutnori.controller.GameSetupController;

import javax.swing.*;
import java.awt.*;

public class GameSetupFrame extends JFrame {
    private final GameSetupDisplay setupDisplay;
    private final GameSetupController controller = new GameSetupController();

    public GameSetupFrame() {
        setTitle("윷놀이 게임 설정");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        
        setupDisplay = new SwingGameSetupDisplay();
        add(setupDisplay.getPanel(), BorderLayout.CENTER);

        // Controller 콜백 등록
        controller.setResultCallback(result -> {
            if (result.success()) {
                JOptionPane.showMessageDialog(GameSetupFrame.this,
                        result.message(), "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(GameSetupFrame.this,
                        result.message(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 게임 시작 버튼 클릭 시 Controller에 데이터 전달
        setupDisplay.setOnStartCallback(data -> {
            controller.handleGameSetup(data);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameSetupFrame frame = new GameSetupFrame();
            frame.setVisible(true);
        });
    }
} 