package com.sw.yutnori.ui.swing.panel;

import com.sw.yutnori.ui.display.SelectionDisplay;
import com.sw.yutnori.ui.swing.display.SwingSelectionDisplay;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class SwingYutSelectionPanel extends JPanel {

    private final SelectionDisplay selectionDisplay;

    public SwingYutSelectionPanel(Consumer<List<String>> onConfirmCallback, Runnable onCancelCallback) {
        setLayout(new BorderLayout());

        // SwingSelectionDisplay 생성 및 콜백 설정
        SwingSelectionDisplay swingDisplay = new SwingSelectionDisplay();
        swingDisplay.setOnConfirmCallback(onConfirmCallback);
        swingDisplay.setOnCancelCallback(onCancelCallback);

        // 인터페이스 참조로 저장
        this.selectionDisplay = swingDisplay;

        // 패널에 추가
        add(swingDisplay.getPanel(), BorderLayout.CENTER);
    }

    //
    public List<String> getSelectedYuts() {
        return selectionDisplay.getSelectedYuts();
    }
}
