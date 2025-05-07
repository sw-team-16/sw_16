package com.sw.yutnori.ui;

import com.sw.yutnori.client.YutnoriApiClient;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;
import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.common.enums.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.List;

public class SwingControlPanel extends JPanel implements GameUI {

    private YutnoriApiClient apiClient;

    private Long gameId;
    private Long playerId;
    private Long currentTurnId = null;
    private Long selectedPieceId = null;

    private JPanel yutPanel;
    private JPanel resultPanel;
    private JPanel currentYutPanel;
    private JPanel buttonPanel;
    private JPanel pieceControlPanel;

    private JButton randomYutBtn;
    private JButton customYutBtn;

    private YutThrowListener yutThrowListener;
    private JLabel currentYutLabel;

    private JLabel[] resultLabels;
    private int currentResultIndex = 0;

    public SwingControlPanel(YutnoriApiClient apiClient) {
        this.apiClient = apiClient;
        initialize();
    }

    public void setGameContext(Long gameId, Long playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    @Override
    public void initialize() {
        initializePanel();
        createComponents();
        layoutComponents();
        setupEventListeners();
    }

    private void initializePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(350, 700));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 70));
    }

    private void createComponents() {
        yutPanel = createYutPanel();
        resultPanel = createResultPanel();
        currentYutPanel = createCurrentYutPanel();
        buttonPanel = createButtonPanel();
        pieceControlPanel = createPieceControlPanel();
    }

    private void layoutComponents() {
        add(yutPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(resultPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(currentYutPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(buttonPanel);
        add(Box.createVerticalGlue());
        add(pieceControlPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void setupEventListeners() {
        randomYutBtn.addActionListener(e -> {
            try {
                Long turnId = getCurrentTurnId();
                AutoThrowResponse response = apiClient.getRandomYutResult(gameId, turnId, playerId);

                if (response.getTurnId() != null) {
                    updateTurnId(response.getTurnId());
                }

                // YutResult 열거형을 String으로 변환
                YutResult yutResult = response.getResult();
                String result = yutResult.name();

                String koreanResult = convertYutTypeToKorean(result);
                displayYutResult(koreanResult);
                updateCurrentYut(result);

                if (yutThrowListener != null) {
                    yutThrowListener.onYutThrown(result);
                }

                randomYutBtn.setEnabled(false);
            } catch (Exception ex) {
                showError("서버 통신 오류: " + ex.getMessage());
            }
        });

        customYutBtn.addActionListener(e -> showCustomYutSelectionPanel());
    }

    private void showCustomYutSelectionPanel() {
        removeAll();
        Consumer<String> onConfirm = this::applyYutSelection;
        Runnable onCancel = this::restoreOriginalPanel;

        YutSelectionPanel selectionPanel = new YutSelectionPanel(onConfirm, onCancel);
        add(selectionPanel);

        revalidate();
        repaint();
    }

    private void restoreOriginalPanel() {
        removeAll();
        layoutComponents();
        revalidate();
        repaint();
    }

    private void applyYutSelection(String selectedYut) {
        try {
            String yutType = convertYutTypeToEnglish(selectedYut);
            YutResult result = convertStringToYutResult(yutType);

            Long turnId = getCurrentTurnId();
            Long pieceId = getSelectedPieceId();

            apiClient.throwYutManual(gameId, turnId, playerId, pieceId, result);

            displayYutResult(selectedYut);
            updateCurrentYut(yutType);

            resetPieceSelection();
            randomYutBtn.setEnabled(false);

            restoreOriginalPanel();
        } catch (Exception ex) {
            showError("서버 통신 오류: " + ex.getMessage());
            restoreOriginalPanel();
        }
    }

    private YutResult convertStringToYutResult(String yutType) {
        return switch (yutType) {
            case "DO" -> YutResult.DO;
            case "GAE" -> YutResult.GAE;
            case "GEOL" -> YutResult.GEOL;
            case "YUT" -> YutResult.YUT;
            case "MO" -> YutResult.MO;
            case "BACKDO" -> YutResult.BACK_DO;
            default -> throw new IllegalArgumentException("알 수 없는 윷 타입: " + yutType);
        };
    }


    private String convertYutType(String yutType, boolean toKorean) {
        return switch (yutType) {
            case "도", "DO" -> toKorean ? "도" : "DO";
            case "개", "GAE" -> toKorean ? "개" : "GAE";
            case "걸", "GEOL" -> toKorean ? "걸" : "GEOL";
            case "윷", "YUT" -> toKorean ? "윷" : "YUT";
            case "모", "MO" -> toKorean ? "모" : "MO";
            case "빽도", "BACKDO", "BACK_DO" -> toKorean ? "빽도" : "BACKDO";
            default -> throw new IllegalArgumentException("알 수 없는 윷 타입: " + yutType);
        };
    }

    private String convertYutTypeToKorean(String englishYutType) {
        return convertYutType(englishYutType, true);
    }

    private String convertYutTypeToEnglish(String koreanYutType) {
        return convertYutType(koreanYutType, false);
    }

    public void startNewTurn() {
        resetResults();
        currentYutLabel.setText("-");
        randomYutBtn.setEnabled(true);
        customYutBtn.setEnabled(true);
    }

    private void resetResults() {
        Arrays.stream(resultLabels).forEach(label -> label.setText("-"));
        currentResultIndex = 0;
    }

    private JPanel createYutPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 0));
        panel.setBorder(BorderFactory.createEmptyBorder());
        panel.setMaximumSize(new Dimension(300, 180));
        panel.setPreferredSize(new Dimension(300, 180));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        panel.setBorder(BorderFactory.createTitledBorder("Current_yut"));
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

        randomYutBtn = createButton("랜덤 윷 던지기", 150, 45);
        randomYutBtn.setName("randomYutBtn");

        customYutBtn = createButton("지정 윷 던지기", 150, 45);
        customYutBtn.setName("customYutBtn");

        panel.add(randomYutBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(customYutBtn);

        return panel;
    }

    private JButton createButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JPanel createPieceControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(300, 150));
        panel.setPreferredSize(new Dimension(300, 150));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        return panel;
    }

    private Long getCurrentTurnId() {
        if (currentTurnId == null) {
            try {
                // 실제 구현에서는 백엔드에서 턴 정보를 가져와야 함
                currentTurnId = 1L;
            } catch (Exception e) {
                showError("턴 정보를 가져오는데 실패했습니다: " + e.getMessage());
                currentTurnId = 1L;
            }
        }
        return currentTurnId;
    }

    public void updateTurnId(Long turnId) {
        this.currentTurnId = turnId;
    }

    private Long getSelectedPieceId() {
        if (selectedPieceId == null) {
            try {
                List<Long> movablePieces = apiClient.getMovablePieces(gameId);
                if (movablePieces == null || movablePieces.isEmpty()) {
                    showError("이동 가능한 말이 없습니다.");
                    return 1L;
                }
                selectedPieceId = movablePieces.get(0);
            } catch (Exception e) {
                showError("말 정보를 가져오는데 실패했습니다: " + e.getMessage());
                return 1L;
            }
        }
        return selectedPieceId;
    }

    public void resetPieceSelection() {
        this.selectedPieceId = null;
    }

    @Override
    public void displayYutResult(String result) {
        if (currentResultIndex >= resultLabels.length) {
            resetResults();
        }
        resultLabels[currentResultIndex].setText(result);
        currentResultIndex++;
    }

    @Override
    public void updateCurrentYut(String yutType) {
        currentYutLabel.setText(yutType);
    }

    @Override
    public void throwRandomYut() {
        // UI 상에서 윷 던지기 애니메이션 등 구현
    }

    @Override
    public void throwCustomYut(String yutType) {
        // 지정된 윷 표시
    }

    @Override
    public void setYutThrowListener(YutThrowListener listener) {
        this.yutThrowListener = listener;
    }

    @Override
    public void updateGameStatus(GameState status) {
        // 게임 상태에 따른 UI 업데이트
    }

    @Override
    public void showWinner(String winnerName) {
        JOptionPane.showMessageDialog(this, winnerName + "님이 승리했습니다!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void closeUI() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
}