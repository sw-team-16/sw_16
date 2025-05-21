package com.sw.yutnori.ui.swing.display;

import com.sw.yutnori.ui.display.ControlDisplay;
import com.sw.yutnori.ui.display.ResultDisplay;
import com.sw.yutnori.ui.display.YutDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SwingControlDisplay implements ControlDisplay {

    private JPanel mainPanel;

    private JPanel yutPanel;
    private JPanel resultPanel;
    private JPanel currentYutPanel;
    private JPanel buttonPanel;

    private JLabel[] yutSticks;
    private ImageIcon upIcon;
    private ImageIcon downIcon;
    private ImageIcon backDoDownIcon;

    private JLabel[] resultLabels;

    private JLabel currentYutLabel;

    private JButton randomYutBtn;
    private JButton customYutBtn;

    private Runnable onRandomYutCallback;
    private Runnable onCustomYutCallback;

    public SwingControlDisplay() {
        initialize();
    }

    private void initialize() {
        initializePanel();
        createComponents();
        layoutComponents();
    }

    private void initializePanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(350, 700));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void createComponents() {
        yutPanel = createYutPanel();
        resultPanel = createResultPanel();
        currentYutPanel = createCurrentYutPanel();
        buttonPanel = createButtonPanel();
    }

    private JPanel createYutPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 0));
        panel.setBorder(BorderFactory.createEmptyBorder());
        panel.setMaximumSize(new Dimension(300, 180));
        panel.setPreferredSize(new Dimension(300, 180));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            upIcon = new ImageIcon(getClass().getResource("/images/yut_up.png"));
            downIcon = new ImageIcon(getClass().getResource("/images/yut_down.png"));
            backDoDownIcon = new ImageIcon(getClass().getResource("/images/yut_backDo_down.png"));

            Image upImg = upIcon.getImage().getScaledInstance(60, 180, Image.SCALE_SMOOTH);
            Image downImg = downIcon.getImage().getScaledInstance(60, 180, Image.SCALE_SMOOTH);
            Image backDoDownImg = backDoDownIcon.getImage().getScaledInstance(60, 180, Image.SCALE_SMOOTH);

            upIcon = new ImageIcon(upImg);
            downIcon = new ImageIcon(downImg);
            backDoDownIcon = new ImageIcon(backDoDownImg);
        } catch (Exception e) {
            upIcon = new ImageIcon(new BufferedImage(60, 180, BufferedImage.TYPE_INT_ARGB));
            downIcon = new ImageIcon(new BufferedImage(60, 180, BufferedImage.TYPE_INT_ARGB));
            backDoDownIcon = new ImageIcon(new BufferedImage(60, 180, BufferedImage.TYPE_INT_ARGB));
        }

        yutSticks = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            yutSticks[i] = new JLabel(upIcon);
            yutSticks[i].setHorizontalAlignment(SwingConstants.CENTER);
            yutSticks[i].setName("yutStick" + i);
            panel.add(yutSticks[i]);
        }

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.setBorder(BorderFactory.createTitledBorder("Result"));
        panel.setMaximumSize(new Dimension(300, 100));
        panel.setPreferredSize(new Dimension(300, 100));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultLabels = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            resultLabels[i] = new JLabel("-");
            resultLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            resultLabels[i].setName("resultLabel" + i);
            panel.add(resultLabels[i]);
        }

        return panel;
    }

    private JPanel createCurrentYutPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("현재 윷"));
        panel.setMaximumSize(new Dimension(150, 80));
        panel.setPreferredSize(new Dimension(150, 80));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        currentYutLabel = new JLabel("-");
        currentYutLabel.setName("currentYutLabel");
        panel.add(currentYutLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setOpaque(false);

        randomYutBtn = createButton("랜덤 윷 던지기");
        randomYutBtn.setName("randomYutBtn");
        customYutBtn = createButton("지정 윷 던지기");
        customYutBtn.setName("customYutBtn");

        randomYutBtn.addActionListener(e -> {
            if (onRandomYutCallback != null) {
                onRandomYutCallback.run();
            }
        });
        customYutBtn.addActionListener(e -> {
            if (onCustomYutCallback != null) {
                onCustomYutCallback.run();
            }
        });

        panel.add(randomYutBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(customYutBtn);

        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 45));
        button.setMaximumSize(new Dimension(150, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    private void layoutComponents() {
        mainPanel.add(yutPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(resultPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(currentYutPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());
    }

    @Override
    public YutDisplay createYutDisplay() {
        return new SwingYutDisplay(yutSticks, upIcon, downIcon, backDoDownIcon);
    }

    @Override
    public ResultDisplay createResultDisplay() {
        return new SwingResultDisplay(resultLabels, currentYutLabel);
    }

    @Override
    public void restorePanel() {
        mainPanel.removeAll();
        layoutComponents();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    @Override
    public void resetCurrentYutLabel() {
        currentYutLabel.setText("-");
    }

    @Override
    public void setOnRandomYutCallback(Runnable callback) {
        this.onRandomYutCallback = callback;
    }

    @Override
    public void setOnCustomYutCallback(Runnable callback) {
        this.onCustomYutCallback = callback;
    }

    @Override
    public void enableRandomButton(boolean enabled) {
        randomYutBtn.setEnabled(enabled);
    }

    @Override
    public void enableCustomButton(boolean enabled) {
        customYutBtn.setEnabled(enabled);
    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }
}
