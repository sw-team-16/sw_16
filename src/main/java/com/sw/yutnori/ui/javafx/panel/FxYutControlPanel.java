package com.sw.yutnori.ui.javafx.panel;

import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.ui.display.ControlDisplay;
import com.sw.yutnori.ui.display.DialogDisplay;
import com.sw.yutnori.ui.display.ResultDisplay;
import com.sw.yutnori.ui.display.YutDisplay;
import com.sw.yutnori.ui.javafx.display.FxControlDisplay;
import com.sw.yutnori.ui.panel.YutControlPanel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class FxYutControlPanel extends VBox implements YutControlPanel {

    private final ControlDisplay controlDisplay;
    private final YutDisplay yutDisplay;
    private final ResultDisplay resultDisplay;
    private final DialogDisplay dialogDisplay;

    private final InGameController controller;

    public FxYutControlPanel(InGameController controller) {
        setSpacing(10);
        setPadding(new Insets(10));

        // 명시적 크기 설정 추가
        setPrefSize(350, 800);
        setMinSize(300, 600);

        this.controller = controller;
        this.dialogDisplay = controller.getDialogDisplay();

        // 디스플레이 생성
        this.controlDisplay = new FxControlDisplay();
        this.yutDisplay = controlDisplay.createYutDisplay();
        this.resultDisplay = controlDisplay.createResultDisplay();

        // 컴포넌트 가져오기 및 안전한 추가
        Node controlComponent = (Node) controlDisplay.getMainComponent();
        if (controlComponent != null) {
            getChildren().add(controlComponent);
        } else {
            dialogDisplay.showErrorDialog("ControlDisplay가 정상적으로 생성되지 않았습니다.");
        }

        // 콜백 설정
        controlDisplay.setOnRandomYutCallback(controller::onRandomYutButtonClicked);
        controlDisplay.setOnCustomYutCallback(this::showCustomYutSelectionPanel);
    }

    // '지정 윷 던지기' 클릭 시 창 변경
    private void showCustomYutSelectionPanel() {
        getChildren().clear();

        Consumer<List<String>> onConfirm = controller::onConfirmButtonClicked;
        Runnable onCancel = this::restorePanel;

        FxYutSelectionPanel selectionPanel = new FxYutSelectionPanel(onConfirm, onCancel);
        getChildren().add(selectionPanel);
    }

    @Override
    public void restorePanel() {
        getChildren().clear();
        getChildren().add((Pane) controlDisplay.getMainComponent());
        controlDisplay.restorePanel();
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

    public Object getMainComponent() {
        return this;
    }
}
