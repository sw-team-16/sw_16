/*
 * GameSetupFrame.java
 * 게임 설정 화면을 구현하는 클래스
 * 
 * 
 * 
 */
package com.sw.yutnori.ui.swing;

import com.sw.yutnori.ui.display.GameSetupDisplay;
import com.sw.yutnori.ui.swing.display.SwingGameSetupDisplay;
import com.sw.yutnori.controller.GameSetupController;
import com.sw.yutnori.controller.InGameController;

import javax.swing.*;
import java.awt.*;

public class SwingGameSetupFrame extends JFrame {
    private final GameSetupDisplay setupDisplay;
    private final GameSetupController controller;

    public SwingGameSetupFrame() {
        setTitle("윷놀이 게임 설정");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        
        setupDisplay = new SwingGameSetupDisplay();
        add(setupDisplay.getPanel(), BorderLayout.CENTER);

        // Controller 생성 및 콜백 등록
        controller = new GameSetupController(this::startGame);
        controller.setResultCallback(result -> {
            if (result.success()) {
                JOptionPane.showMessageDialog(SwingGameSetupFrame.this,
                        result.message(), "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(SwingGameSetupFrame.this,
                        result.message(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 게임 시작 버튼 클릭 시 Controller에 데이터 전달
        setupDisplay.setOnStartCallback(data -> {
            controller.handleGameSetup(data);
        });
    }

    private void startGame(InGameController inGameController) {
        this.setVisible(false);
        // 게임 화면 생성 및 표시
        SwingInGameFrame gameFrame = new SwingInGameFrame(inGameController);
        gameFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingGameSetupFrame frame = new SwingGameSetupFrame();
            frame.setVisible(true);
        });
    }
} 