package com.sw.yutnori.client.view;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private final JTextArea logArea;

    public LogPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Log"));

        logArea = new JTextArea(10, 20);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
    }

    public void appendLog(String text) {
        logArea.append(text + "\n");
    }
}
