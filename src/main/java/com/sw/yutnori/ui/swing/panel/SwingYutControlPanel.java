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

    private Long playerId;

    private final InGameController controller;
    private final YutDisplay yutDisplay;
    private final ResultDisplay resultDisplay;

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

    // 플레이어 ID 설정
    public void setGameContext(Long playerId) {
        this.playerId = playerId;
    }

    // 선택 UI 또는 패널 활성화 로직
    public void enableYutSelection() {
        System.out.println("윷 선택 UI를 활성화합니다.");
    }

    // '지정 윷 던지기' 클릭 시 창 변경
    private void showCustomYutSelectionPanel() {
        removeAll();

        Consumer<List<String>> onConfirm = selectedYuts -> {
            controller.promptPieceSelection(playerId); // 말 선택 창 띄움
            controller.onConfirmButtonClicked(selectedYuts); // '완료' 버튼 클릭 시 발생하는 이벤트

        };
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
        JOptionPane.showMessageDialog(this, winnerName + "님이 승리했습니다!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
        String message = winnerName + "님이 승리했습니다!";
        String[] options = {"재시작", "종료"};
        int choice = JOptionPane.showOptionDialog(
            this,
            message,
            "게임 종료",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
        if (choice == JOptionPane.YES_OPTION) {
            // Controller logic is now handled in InGameController
        } else if (choice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
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

    // 윷 결과 업데이트
    public void updateYutResult(String koreanResult, String result) {
        displayYutResult(koreanResult);
        updateCurrentYut(result);
        updateYutSticks(result);
    }

    public void displayYutResult(String result) {
        resultDisplay.displayYutResult(result);
    }

    public void updateCurrentYut(String yutType) {
        resultDisplay.updateCurrentYut(yutType);
        yutDisplay.displayYutResult(yutType);
    }

    public void updateYutSticks(String yutType) {
        yutDisplay.displayYutResult(yutType);
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
}
