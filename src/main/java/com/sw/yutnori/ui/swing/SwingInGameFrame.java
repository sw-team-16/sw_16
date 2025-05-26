package com.sw.yutnori.ui.swing;

import com.sw.yutnori.ui.swing.panel.SwingStatusPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutBoardPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutControlPanel;
import com.sw.yutnori.controller.InGameController;

import javax.swing.*;
import java.awt.*;

public class SwingInGameFrame extends JFrame {
    private final SwingYutBoardPanel yutBoardPanel;
    private final SwingYutControlPanel controlPanel;
    private final SwingStatusPanel statusPanel;

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

        yutBoardPanel.setPreferredSize(new Dimension(boardPanelWidth, boardPanelHeight));
        controlPanel.setPreferredSize(new Dimension(controlPanelWidth, controlPanelHeight));
        statusPanel.setPreferredSize(new Dimension(1500, statusPanelHeight));

        add(yutBoardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
