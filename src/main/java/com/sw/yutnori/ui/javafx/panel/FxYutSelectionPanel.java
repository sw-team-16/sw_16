package com.sw.yutnori.ui.javafx.panel;

import com.sw.yutnori.ui.javafx.display.FxSelectionDisplay;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class FxYutSelectionPanel extends VBox {

    public FxYutSelectionPanel(Consumer<List<String>> onConfirmCallback, Runnable onCancelCallback) {
        setSpacing(10);

        // FxSelectionDisplay 생성 및 콜백 설정
        FxSelectionDisplay selectionDisplay = new FxSelectionDisplay();
        selectionDisplay.setOnConfirmCallback(onConfirmCallback);
        selectionDisplay.setOnCancelCallback(onCancelCallback);

        // 패널에 추가
        getChildren().add((Pane) selectionDisplay.getMainComponent());
    }
}
