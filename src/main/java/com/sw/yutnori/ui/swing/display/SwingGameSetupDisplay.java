/*
 * SwingGameSetupDisplay.java
 * 게임 설정 화면을 Swing 프레임으로 구현
 * 
 * 
 * 
 */
package com.sw.yutnori.ui.swing.display;

import com.sw.yutnori.ui.display.GameSetupDisplay;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SwingGameSetupDisplay implements GameSetupDisplay {
    private final JPanel panel;
    private final JComboBox<String> boardTypeCombo;
    private final JComboBox<Integer> playerCountCombo;
    private final JComboBox<Integer> pieceCountCombo;
    private Consumer<SetupData> onStartCallback;
    private JPanel playersPanel;
    private java.util.List<JTextField> playerNameFields;
    private java.util.List<JComboBox<String>> playerColorCombos;
    private final String[] allColors = {"RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "PURPLE", "BLACK", "WHITE"};

    public SwingGameSetupDisplay() {
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 보드 타입 선택
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("보드 타입:"), gbc);
        
        String[] boardTypes = {"사각형", "오각형", "육각형"};
        boardTypeCombo = new JComboBox<>(boardTypes);
        gbc.gridx = 1;
        panel.add(boardTypeCombo, gbc);

        // 플레이어 수 선택
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("플레이어 수:"), gbc);
        Integer[] playerCounts = {2, 3, 4};
        playerCountCombo = new JComboBox<>(playerCounts);
        gbc.gridx = 1;
        panel.add(playerCountCombo, gbc);

        // 플레이어 입력 패널
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        panel.add(playersPanel, gbc);
        gbc.gridwidth = 1;

        playerNameFields = new java.util.ArrayList<>();
        playerColorCombos = new java.util.ArrayList<>();
        updatePlayerInputs((Integer) playerCountCombo.getSelectedItem());
        playerCountCombo.addActionListener(e -> {
            int count = (Integer) playerCountCombo.getSelectedItem();
            updatePlayerInputs(count);
        });

        // 말 개수 선택
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("말 개수:"), gbc);
        Integer[] pieceCounts = {2, 3, 4, 5};
        pieceCountCombo = new JComboBox<>(pieceCounts);
        gbc.gridx = 1;
        panel.add(pieceCountCombo, gbc);

        // 시작 버튼
        JButton startButton = new JButton("게임 시작");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(startButton, gbc);
        gbc.gridwidth = 1;

        startButton.addActionListener(e -> {
            if (onStartCallback != null) {
                String boardType = (String) boardTypeCombo.getSelectedItem();
                int playerCount = (Integer) playerCountCombo.getSelectedItem();
                int pieceCount = (Integer) pieceCountCombo.getSelectedItem();
                java.util.List<GameSetupDisplay.PlayerInfo> players = new java.util.ArrayList<>();
                for (int i = 0; i < playerCount; i++) {
                    String name = playerNameFields.get(i).getText();
                    String color = (String) playerColorCombos.get(i).getSelectedItem();
                    players.add(new GameSetupDisplay.PlayerInfo(name, color));
                }
                onStartCallback.accept(new SetupData(boardType, playerCount, pieceCount, players));
            }
        });
    }

    private void updatePlayerInputs(int count) {
        playersPanel.removeAll();
        playerNameFields.clear();
        playerColorCombos.clear();
        for (int i = 0; i < count; i++) {
            JPanel p = new JPanel();
            p.add(new JLabel("플레이어 " + (i + 1) + " 이름:"));
            JTextField nameField = new JTextField(7);
            if (i == 0) nameField.setText("Player1");
            else if (i == 1) nameField.setText("Player2");
            playerNameFields.add(nameField);
            p.add(nameField);
            p.add(new JLabel("색상:"));
            JComboBox<String> colorCombo = new JComboBox<>(getAvailableColors(i, null));
            int idx = i;
            colorCombo.addActionListener(e -> updateColorCombos(idx));
            playerColorCombos.add(colorCombo);
            p.add(colorCombo);
            playersPanel.add(p);
        }
        playersPanel.revalidate();
        playersPanel.repaint();
    }

    private void updateColorCombos(int changedIdx) {
        // Get all selected colors
        java.util.Set<String> selectedColors = new java.util.HashSet<>();
        for (int i = 0; i < playerColorCombos.size(); i++) {
            if (i == changedIdx) continue;
            String sel = (String) playerColorCombos.get(i).getSelectedItem();
            if (sel != null) selectedColors.add(sel);
        }
        // Update all combos except the one just changed
        for (int i = 0; i < playerColorCombos.size(); i++) {
            if (i == changedIdx) continue;
            String current = (String) playerColorCombos.get(i).getSelectedItem();
            playerColorCombos.get(i).setModel(new DefaultComboBoxModel<>(getAvailableColors(i, current)));
            if (current != null) playerColorCombos.get(i).setSelectedItem(current);
        }
    }

    private String[] getAvailableColors(int idx, String current) {
        java.util.Set<String> used = new java.util.HashSet<>();
        for (int i = 0; i < playerColorCombos.size(); i++) {
            if (i == idx) continue;
            String sel = (String) (i < playerColorCombos.size() ? playerColorCombos.get(i).getSelectedItem() : null);
            if (sel != null) used.add(sel);
        }
        java.util.List<String> available = new java.util.ArrayList<>();
        for (String color : allColors) {
            if (!used.contains(color) || (current != null && color.equals(current))) {
                available.add(color);
            }
        }
        return available.toArray(new String[0]);
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void setOnStartCallback(Consumer<SetupData> callback) {
        this.onStartCallback = callback;
    }
} 