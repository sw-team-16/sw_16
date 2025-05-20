/*
 * SwingControlPanel.java
 * 윷놀이 게임 컨트롤 패널 클래스
 *  ui 화면만 구현
 * 
 * 
 * 
 */
package com.sw.yutnori.ui.swing.panel;

import com.sw.yutnori.ui.swing.SwingGameSetupFrame;
import com.sw.yutnori.ui.display.*;
import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.ui.swing.display.SwingControlDisplay;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.List;

public class SwingYutControlPanel extends JPanel {

    private final ControlDisplay controlDisplay;
    private final YutDisplay yutDisplay;
    private final ResultDisplay resultDisplay;

    private final InGameController controller;

    public SwingYutControlPanel(InGameController controller) {
        setLayout(new BorderLayout());

        this.controlDisplay = new SwingControlDisplay();
        this.yutDisplay = controlDisplay.createYutDisplay();
        this.resultDisplay = controlDisplay.createResultDisplay();

        add(controlDisplay.getPanel(), BorderLayout.CENTER);

        this.controller = controller;

        controlDisplay.setOnRandomYutCallback(controller::onRandomYutButtonClicked);
        controlDisplay.setOnCustomYutCallback(this::showCustomYutSelectionPanel);
    }

    // 선택 UI 또는 패널 활성화 로직
    public void enableYutSelection() {
        System.out.println("윷 선택 UI를 활성화합니다.");
    }

    // '지정 윷 던지기' 클릭 시 창 변경
    private void showCustomYutSelectionPanel() {
        removeAll();

        Consumer<List<String>> onConfirm = controller::onConfirmButtonClicked;
        Runnable onCancel = this::restorePanel;

        SwingYutSelectionPanel selectionPanel = new SwingYutSelectionPanel(onConfirm, onCancel);
        add(selectionPanel);
        revalidate();
        repaint();
    }

    public void restorePanel() {
        removeAll();
        add(controlDisplay.getPanel(), BorderLayout.CENTER);
        controlDisplay.restorePanel();
        revalidate();
        repaint();
    }

    public void startNewTurn() {
        yutDisplay.reset();
        resultDisplay.resetResults();
        controlDisplay.resetCurrentYutLabel();
        enableRandomButton(true);
        enableCustomButton(true);
    }

    public ResultDisplay getResultDisplay() {
        return resultDisplay;
    }

    public void showWinnerDialog(String winnerName) {
        Window window = SwingUtilities.getWindowAncestor(this);
        JFrame frame = (window instanceof JFrame) ? (JFrame) window : null;
        if (frame == null) {
            JOptionPane.showMessageDialog(this, winnerName + "님이 승리했습니다!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 오버레이(GlassPane) 생성
        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 120)); // 반투명 overlay
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setLayout(new GridBagLayout());

        // 중앙 게임 종료 Panel 생성
        JPanel dialogPanel = new JPanel();
        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setPreferredSize(new Dimension(350, 200));

        JLabel label = new JLabel(winnerName + "님이 승리했습니다!");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(36, 0, 30, 0));
        dialogPanel.add(label);

        dialogPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        JButton restartBtn = new JButton("재시작");
        JButton exitBtn = new JButton("종료");
        restartBtn.setPreferredSize(new Dimension(100, 38));
        exitBtn.setPreferredSize(new Dimension(100, 38));
        buttonPanel.add(restartBtn);
        buttonPanel.add(exitBtn);
        dialogPanel.add(buttonPanel);

        // 버튼 동작
        restartBtn.addActionListener(e -> {
            frame.getGlassPane().setVisible(false);
            closeWindowAndOpenSetup();
        });
        exitBtn.addActionListener(e -> System.exit(0));

        // 오버레이에 다이얼로그 중앙 배치
        overlay.add(dialogPanel, new GridBagConstraints());
        frame.setGlassPane(overlay);
        overlay.setVisible(true);
        overlay.requestFocusInWindow();
    }

    public void closeWindowAndOpenSetup() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            SwingGameSetupFrame frame = new SwingGameSetupFrame();
            frame.setVisible(true);
        });
    }

    // 윷 및 결과창 업데이트
    public void updateDisplay(String result) {
        yutDisplay.displayYutSticks(result);
        resultDisplay.updateCurrentYut(result);
    }

    // 랜덤 윷 버튼 활성화 및 비활성화
    public void enableRandomButton(boolean enabled) {
        controlDisplay.enableRandomButton(enabled);
    }

    // 지정 윷 버튼 활성화 및 비활성화
    public void enableCustomButton(boolean enabled) {
        controlDisplay.enableCustomButton(enabled);
    }

    // 오류 메시지 표시 및 원래 패널로 복원
    public void showErrorAndRestore(String message) {
        showError(message);
        controlDisplay.restorePanel();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }
    private boolean lastUsedRandom = false;

    public void markUsedRandomButton() {
        lastUsedRandom = true;
    }

    public void markUsedCustomButton() {
        lastUsedRandom = false;
    }

    public boolean wasRandomYutButtonUsed() {
        return lastUsedRandom;
    }

}
