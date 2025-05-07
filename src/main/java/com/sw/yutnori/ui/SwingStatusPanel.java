package com.sw.yutnori.ui;

import javax.swing.*;
import java.awt.*;

// 임시 제작
public class SwingStatusPanel extends JPanel {
    public SwingStatusPanel() {
        setLayout(new GridLayout(1, 4));
        setBorder(BorderFactory.createTitledBorder("Status"));

        add(createPlayerPanel("Player 1", Color.PINK));
        add(createPlayerPanel("Player 2", Color.CYAN));
        add(createPlayerPanel("Player 3", null));
        add(createPlayerPanel("Player 4", null));
    }

    private JPanel createPlayerPanel(String name, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder(name));

        if (color != null) {
            for (int i = 0; i < 4; i++) {
                JLabel piece = new JLabel();
                piece.setPreferredSize(new Dimension(20, 20));
                piece.setOpaque(true);
                piece.setBackground(color);
                panel.add(piece);
            }
        }

        return panel;
    }
}
