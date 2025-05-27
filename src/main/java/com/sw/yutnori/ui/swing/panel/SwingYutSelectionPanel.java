package com.sw.yutnori.ui.swing.panel;

import com.sw.yutnori.ui.swing.display.SwingSelectionDisplay;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class SwingYutSelectionPanel extends JPanel {

    public SwingYutSelectionPanel(Consumer<List<String>> onConfirmCallback, Runnable onCancelCallback) {
        setLayout(new BorderLayout());

        // SwingSelectionDisplay 생성 및 콜백 설정
        SwingSelectionDisplay swingDisplay = new SwingSelectionDisplay();
        swingDisplay.setOnConfirmCallback(onConfirmCallback);
        swingDisplay.setOnCancelCallback(onCancelCallback);

        // 패널에 추가
        add((JPanel)swingDisplay.getMainComponent(), BorderLayout.CENTER);
    }
}
