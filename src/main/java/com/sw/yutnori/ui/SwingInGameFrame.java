package com.sw.yutnori.ui;

import javax.swing.*;
import java.awt.*;

public class SwingInGameFrame extends JFrame {
    public SwingInGameFrame() {
        setTitle("In-Game Frame");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

//        add(new BoardPanel(), BorderLayout.CENTER);
        add(new SwingControlPanel(), BorderLayout.EAST);

        SwingStatusPanel statusPanel = new SwingStatusPanel();
        statusPanel.setPreferredSize(new Dimension(statusPanel.getPreferredSize().width, 100));
        add(statusPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingInGameFrame::new);
    }
}
