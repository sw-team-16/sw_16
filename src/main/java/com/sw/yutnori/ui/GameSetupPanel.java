package com.sw.yutnori.ui;

import javax.swing.*;
import java.awt.*;

public class GameSetupPanel extends JPanel {
    private JComboBox<Integer> playerCountCombo;
    private JComboBox<Integer> pieceCountCombo;
    private JComboBox<String> boardTypeCombo;
    private JButton startButton;

    public GameSetupPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("윷놀이 게임 설정", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("플레이어 수:"), gbc);
        playerCountCombo = new JComboBox<>(new Integer[]{2, 3, 4});
        gbc.gridx = 1;
        add(playerCountCombo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("말 개수:"), gbc);
        pieceCountCombo = new JComboBox<>(new Integer[]{2, 3, 4, 5});
        gbc.gridx = 1;
        add(pieceCountCombo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("윷판 형태:"), gbc);
        boardTypeCombo = new JComboBox<>(new String[]{"사각형", "오각형", "육각형"});
        gbc.gridx = 1;
        add(boardTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        startButton = new JButton("게임 시작");
        add(startButton, gbc);
    }

    public int getPlayerCount() {
        return (Integer) playerCountCombo.getSelectedItem();
    }
    public int getPieceCount() {
        return (Integer) pieceCountCombo.getSelectedItem();
    }
    public String getBoardType() {
        return (String) boardTypeCombo.getSelectedItem();
    }
    public JButton getStartButton() {
        return startButton;
    }
}
