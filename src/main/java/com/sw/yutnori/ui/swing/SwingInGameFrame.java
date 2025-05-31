package com.sw.yutnori.ui.swing;

import com.sw.yutnori.ui.panel.StatusPanel;
import com.sw.yutnori.ui.panel.YutBoardPanel;
import com.sw.yutnori.ui.panel.YutControlPanel;
import com.sw.yutnori.controller.InGameController;

import javax.swing.*;
import java.awt.*;

public class SwingInGameFrame extends JFrame {
    private final YutBoardPanel yutBoardPanel;
    private final YutControlPanel controlPanel;
    private final StatusPanel statusPanel;

    public SwingInGameFrame(InGameController controller) {
        setTitle("윷놀이 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setSize(1500, 1000);

        int boardPanelWidth = 1000;
        int boardPanelHeight = 800;
        int controlPanelWidth = 350;
        int controlPanelHeight = 900;
        int statusPanelHeight = 80;

        this.yutBoardPanel = controller.getYutBoardPanel();
        this.controlPanel = controller.getControlPanel();
        this.statusPanel = controller.getStatusPanel();

        if (yutBoardPanel instanceof JComponent) {
            ((JComponent) yutBoardPanel).setPreferredSize(new Dimension(boardPanelWidth, boardPanelHeight));
        }
        if (controlPanel instanceof JComponent) {
            ((JComponent) controlPanel).setPreferredSize(new Dimension(controlPanelWidth, controlPanelHeight));
        }
        if (statusPanel instanceof JComponent) {
            ((JComponent) statusPanel).setPreferredSize(new Dimension(1500, statusPanelHeight));
        }

        if (yutBoardPanel instanceof JComponent) {
            add((JComponent) yutBoardPanel, BorderLayout.CENTER);
        }
        if (controlPanel instanceof JComponent) {
            add((JComponent) controlPanel, BorderLayout.EAST);
        }
        if (statusPanel instanceof JComponent) {
            add((JComponent) statusPanel, BorderLayout.SOUTH);
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
