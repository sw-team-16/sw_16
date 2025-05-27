/*
 * SwingControlPanel.java
 * 윷놀이 게임 컨트롤 패널 클래스
 *  ui 화면만 구현
 * 
 * 
 * 
 */
package com.sw.yutnori.ui.swing.panel;

import com.sw.yutnori.ui.panel.YutControlPanel;
import com.sw.yutnori.ui.swing.SwingGameSetupFrame;
import com.sw.yutnori.ui.display.*;
import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.ui.swing.display.SwingControlDisplay;
import com.sw.yutnori.ui.swing.display.SwingDialogDisplay;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.List;

public class SwingYutControlPanel extends JPanel implements YutControlPanel {

    private final ControlDisplay controlDisplay;
    private final YutDisplay yutDisplay;
    private final ResultDisplay resultDisplay;
    private final DialogDisplay dialogDisplay;

    private final InGameController controller;

    public SwingYutControlPanel(InGameController controller) {
        setLayout(new BorderLayout());

        this.controlDisplay = new SwingControlDisplay();
        this.yutDisplay = controlDisplay.createYutDisplay();
        this.resultDisplay = controlDisplay.createResultDisplay();

        add((JPanel)controlDisplay.getMainComponent(), BorderLayout.CENTER);

        this.controller = controller;
        this.dialogDisplay = controller.getDialogDisplay();
        ((SwingDialogDisplay)dialogDisplay).setParentComponent(this);

        controlDisplay.setOnRandomYutCallback(controller::onRandomYutButtonClicked);
        controlDisplay.setOnCustomYutCallback(this::showCustomYutSelectionPanel);
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

    @Override
    public void restorePanel() {
        removeAll();
        add((JPanel)controlDisplay.getMainComponent(), BorderLayout.CENTER);
        controlDisplay.restorePanel();
        revalidate();
        repaint();
    }

    @Override
    public void startNewTurn() {
        yutDisplay.reset();
        resultDisplay.resetResults();
        controlDisplay.resetCurrentYutLabel();
        enableRandomButton(true);
        enableCustomButton(true);
    }

    @Override
    public ResultDisplay getResultDisplay() {
        return resultDisplay;
    }

    @Override
    public void updateDisplay(String result) {
        yutDisplay.displayYutSticks(result);
        resultDisplay.updateCurrentYut(result);
    }

    // 랜덤 윷 버튼 활성화 및 비활성화
    @Override
    public void enableRandomButton(boolean enabled) {
        controlDisplay.enableRandomButton(enabled);
    }

    // 지정 윷 버튼 활성화 및 비활성화
    @Override
    public void enableCustomButton(boolean enabled) {
        controlDisplay.enableCustomButton(enabled);
    }

    // 오류 메시지 표시 및 원래 패널로 복원
    @Override
    public void showErrorAndRestore(String message) {
        dialogDisplay.showErrorDialog(message);
        controlDisplay.restorePanel();
    }
}
