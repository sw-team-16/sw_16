package com.sw.yutnori.client.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private final GameBoardPanel boardPanel;
    private final ControlPanel controlPanel;

    public GameWindow() {
        setTitle("Yutnori Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        boardPanel = new GameBoardPanel();
        controlPanel = new ControlPanel();

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}

class GameBoardPanel extends JPanel {
    public GameBoardPanel() {
        setPreferredSize(new Dimension(700, 700));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createTitledBorder("Game Board"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Placeholder for board drawing logic
        g.setColor(Color.GRAY);
        g.drawRect(50, 50, 600, 600);
        g.drawString("Game board goes here", 300, 350);
    }
}

class ControlPanel extends JPanel {
    private final PlayerInfoPanel playerInfoPanel;
    private final YutThrowPanel yutThrowPanel;
    private LogPanel logPanel;

    public ControlPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 700));
        setBorder(BorderFactory.createTitledBorder("Controls"));

        playerInfoPanel = new PlayerInfoPanel();
        yutThrowPanel = new YutThrowPanel();
        logPanel = new LogPanel();

        add(playerInfoPanel);
        add(yutThrowPanel);
        add(logPanel);
    }
}

class PlayerInfoPanel extends JPanel {
    private final JLabel nameLabel;
    private final JLabel colorLabel;

    public PlayerInfoPanel() {
        setLayout(new GridLayout(2, 1));
        setBorder(BorderFactory.createTitledBorder("Player Info"));

        nameLabel = new JLabel("Name: -");
        colorLabel = new JLabel("Color: -");

        add(nameLabel);
        add(colorLabel);
    }

    public void updateInfo(String name, String color) {
        nameLabel.setText("Name: " + name);
        colorLabel.setText("Color: " + color);
    }
}

class YutThrowPanel extends JPanel {
    @Getter
    private JButton throwButton;
    private JComboBox<String> resultBox;

    public YutThrowPanel() {
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder("Throw Yut"));

        resultBox = new JComboBox<>(new String[]{"DO", "GAE", "GEOL", "YUT", "MO"});
        throwButton = new JButton("Throw");

        add(resultBox);
        add(throwButton);
    }

    public String getSelectedResult() {
        return (String) resultBox.getSelectedItem();
    }
}

class LogPanel extends JPanel {
    private final JTextArea logArea;

    public LogPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Game Log"));

        logArea = new JTextArea(10, 25);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
    }

    public void appendLog(String message) {
        logArea.append(message + "\n");
    }
}