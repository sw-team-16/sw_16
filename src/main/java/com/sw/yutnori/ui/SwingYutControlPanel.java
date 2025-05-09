/*
 * SwingControlPanel.java
 * 윷놀이 게임 컨트롤 패널 클래스
 *  ui 화면만 구현
 * 
 * 
 * 
 */
package com.sw.yutnori.ui;

import com.sw.yutnori.client.GameApiClient;
import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.ui.display.ResultDisplay;
import com.sw.yutnori.ui.display.SwingResultDisplay;
import com.sw.yutnori.ui.display.SwingYutDisplay;
import com.sw.yutnori.ui.display.YutDisplay;
import com.sw.yutnori.controller.InGameController;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.Image;

public class SwingYutControlPanel extends JPanel implements GameUI {

    private Long gameId;
    private Long playerId;
    private Long currentTurnId = null;
    private Long selectedPieceId = null;

    private JPanel yutPanel;
    private JPanel resultPanel;
    private JPanel currentYutPanel;
    private JPanel buttonPanel;

    private JButton randomYutBtn;
    private JButton customYutBtn;

    private JLabel currentYutLabel;
    private JLabel[] resultLabels;

    private JLabel[] yutSticks;
    private ImageIcon upIcon;
    private ImageIcon downIcon;
    private ImageIcon backDoDownIcon;

    private final GameApiClient apiClient;
    private final InGameController controller;
    private final YutDisplay yutDisplay;
    private final ResultDisplay resultDisplay;

    public SwingYutControlPanel(GameApiClient apiClient, InGameController controller) {
        this.apiClient = apiClient;
        this.controller = controller;
        initialize();
        this.yutDisplay = new SwingYutDisplay(yutSticks, upIcon, downIcon, backDoDownIcon);
        this.resultDisplay = new SwingResultDisplay(resultLabels, currentYutLabel);
    }

    // 게임 ID와 플레이어 ID를 설정 - 턴이 진행되는 방식에 맞추어 변경되어야 함
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
    }

    // '랜덤 윷 던지기' 및 '지정 윷 던지기' 버튼 클릭 시 발생하는 이벤트 초기화
    private void setupEventListeners() {
        randomYutBtn.addActionListener(e -> controller.onRandomYutButtonClicked());
        customYutBtn.addActionListener(e -> showCustomYutSelectionPanel());
    }

    // '지정 윷 던지기' 클릭 시 창 변경
    private void showCustomYutSelectionPanel() {
        removeAll();
        Consumer<List<String>> onConfirm = selectedYuts -> {
            controller.onCustomYutButtonClicked(selectedYuts);
        };
        Runnable onCancel = this::restoreOriginalPanel;
        SwingYutSelectionPanel selectionPanel = new SwingYutSelectionPanel(onConfirm, onCancel);
        add(selectionPanel);
        revalidate();
        repaint();
    }

    private YutResult convertStringToYutResult(String yutType) {
        return switch (yutType) {
            case "DO" -> YutResult.DO;
            case "GAE" -> YutResult.GAE;
            case "GEOL" -> YutResult.GEOL;
            case "YUT" -> YutResult.YUT;
            case "MO" -> YutResult.MO;
            case "BACK_DO" -> YutResult.BACK_DO;
            default -> throw new IllegalArgumentException("알 수 없는 윷 타입: " + yutType);
        };
    }

    // '지정 윷 던지기'에서 취소/완료 후 원래 패널로 복원
    private void restoreOriginalPanel() {
        removeAll();
        layoutComponents();
        revalidate();
        repaint();
    }

    public void restorePanel() {
        restoreOriginalPanel();
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
            resultLabels[i].setFont(new Font("맑은 고딕", Font.BOLD, 32));
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
        currentYutLabel.setFont(new Font("맑은 고딕", Font.BOLD, 32));
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

    // !TODO: 게임 턴이 변경될 때 호출 - 아직 미적용
    public void startNewTurn() {
        resultDisplay.resetResults();
        currentYutLabel.setText("-");
        randomYutBtn.setEnabled(true);
        customYutBtn.setEnabled(true);
    }

    // 게임 진행 관련
    private Long getCurrentTurnId() {
        if (currentTurnId == null) {
            try {
                // !TODO: 턴 정보 로직 구현 필요
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
        return selectedPieceId;
    }

    public void resetPieceSelection() {
        this.selectedPieceId = null;
    }

    // GameUI 인터페이스 메소드 구현
    @Override
    public void displayYutResult(String result) {
        resultDisplay.displayYutResult(result);

        // 버튼 활성화/비활성화 로직
        randomYutBtn.setEnabled(resultDisplay.isSpecialResult(result));
    }

    @Override
    public void updateCurrentYut(String yutType) {
        resultDisplay.updateCurrentYut(yutType);
        yutDisplay.displayYutResult(yutType);
    }

    @Override
    public void updateYutSticks(String yutType) {
        yutDisplay.displayYutResult(yutType);
    }

    @Override
    public void showWinner(String winnerName) {
        // Controller logic is now handled in InGameController
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

    public ResultDisplay getResultDisplay() {
        return resultDisplay;
    }

    public void setRandomYutButtonEnabled(boolean enabled) {
        randomYutBtn.setEnabled(enabled);
    }

    public void showWinnerDialog(String winnerName) {
        JOptionPane.showMessageDialog(this, winnerName + "님이 승리했습니다!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
        String message = winnerName + "님이 승리했습니다!";
        String[] options = {"재시작", "종료"};
        int choice = JOptionPane.showOptionDialog(
            this,
            message,
            "게임 종료",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
        if (choice == JOptionPane.YES_OPTION) {
            // Controller logic is now handled in InGameController
        } else if (choice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    public void closeWindowAndOpenSetup() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            SwingGameSetupFrame frame = new SwingGameSetupFrame();
            frame.setVisible(true);
        });
    }

    // 윷 결과 업데이트
    public void updateYutResult(String koreanResult, String result) {
        displayYutResult(koreanResult);
        updateCurrentYut(result);
        updateYutSticks(result);
    }

    // 랜덤 윷 버튼 활성화
    public void enableRandomButton(boolean enabled) {
        randomYutBtn.setEnabled(enabled);
    }

    // 오류 메시지 표시 및 원래 패널로 복원
    public void showErrorAndRestore(String message) {
        showError(message);
        restoreOriginalPanel();
    }
}
