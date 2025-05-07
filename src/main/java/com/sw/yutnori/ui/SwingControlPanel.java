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
import java.util.Random;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.Image;

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

    private JLabel currentYutLabel;
    private JLabel[] resultLabels;
    private int currentResultIndex = 0;

    private JLabel[] yutSticks;
    private ImageIcon upIcon;
    private ImageIcon downIcon;
    private ImageIcon backDoDownIcon;

    public SwingControlPanel(YutnoriApiClient apiClient) {
        this.apiClient = apiClient;
        initialize();
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

                // 윷이나 모가 나왔을 경우에는 버튼을 활성화 상태로 유지
                if (yutResult != YutResult.YUT && yutResult != YutResult.MO) {
                    randomYutBtn.setEnabled(false);
                }
            } catch (Exception ex) {
                showError("서버 통신 오류: " + ex.getMessage());
            }
        });

        customYutBtn.addActionListener(e -> showCustomYutSelectionPanel());
    }

    // '지정 윷 던지기' 클릭 시 발생하는 이벤트
    private void showCustomYutSelectionPanel() {
        removeAll();

        // 완료 콜백 - 선택된 윷 결과를 매개변수로 전달받음
        Consumer<List<String>> onConfirm = selectedYuts -> {
            try {
                if (selectedYuts.isEmpty()) {
                    showError("선택된 윷 결과가 없습니다.");
                    restoreOriginalPanel();
                    return;
                }

                Long turnId = getCurrentTurnId();
                Long pieceId = getSelectedPieceId();

                // 모든 선택된 결과들을 백엔드로 전송
                for (String selectedYut : selectedYuts) {
                    // 한국어 윷 결과를 백엔드 전송용 영어로 변환
                    String yutType = convertYutTypeToEnglish(selectedYut);
                    YutResult result = convertStringToYutResult(yutType);

                    // 백엔드 API 호출
                    apiClient.throwYutManual(gameId, turnId, playerId, pieceId, result);

                    displayYutResult(selectedYut);
                }

                // 마지막 선택 윷을 현재 윷으로 표시
                String lastYutType = convertYutTypeToEnglish(selectedYuts.get(selectedYuts.size() - 1));
                updateCurrentYut(lastYutType);

                resetPieceSelection();
                randomYutBtn.setEnabled(false);
                restoreOriginalPanel();

            } catch (Exception ex) {
                showError("서버 통신 오류: " + ex.getMessage());
                restoreOriginalPanel();
            }
        };

        // 취소 콜백
        Runnable onCancel = this::restoreOriginalPanel;

        YutSelectionPanel selectionPanel = new YutSelectionPanel(onConfirm, onCancel);
        add(selectionPanel);

        revalidate();
        repaint();
    }

    // '지정 윷 던지기'에서 취소/완료 후 원래 패널로 복원
    private void restoreOriginalPanel() {
        removeAll();
        layoutComponents();
        revalidate();
        repaint();
    }

    // String -> YutResult 변환
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

    // 한영 변환
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

    public static String formatYutResultWithSuperscript(String result, String currentText) {
        // HTML 태그가 있는지 확인
        if (currentText.startsWith("<html>")) {
            // 기존 텍스트에서 결과와 횟수 추출
            String content = currentText.substring(6, currentText.length() - 7);
            int startIndex = content.indexOf("<sup>");

            if (startIndex > 0) {
                String baseText = content.substring(0, startIndex);
                String countText = content.substring(startIndex + 5, content.indexOf("</sup>"));

                // 같은 결과인 경우에만 횟수 증가
                if (baseText.equals(result)) {
                    try {
                        int count = Integer.parseInt(countText);
                        return "<html>" + result + "<sup>" + (count + 1) + "</sup></html>";
                    } catch (NumberFormatException e) {
                        return "<html>" + result + "<sup>2</sup></html>";
                    }
                }
            }
        }

        // 처음 중복되는 경우 - 횟수 2부터 시작
        return "<html>" + result + "<sup>2</sup></html>";
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
                // 턴 정보 로직 구현 필요
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

    @Override
    public void displayYutResult(String result) {
        // 윷이나 모인 경우 특별 처리
        if (result.equals("윷") || result.equals("모")) {
            // 현재 칸의 텍스트 확인
            String currentText = resultLabels[currentResultIndex].getText();

            // 현재 칸이 비어있으면 결과 표시
            if (currentText.equals("-")) {
                resultLabels[currentResultIndex].setText(result);
                // 버튼 활성화 상태 유지
                randomYutBtn.setEnabled(true);
                return;
            }

            // 현재 칸에 같은 결과가 있으면 위첨자로 표시
            if (currentText.equals(result) ||
                    (currentText.startsWith("<html>") && currentText.contains(result))) {
                resultLabels[currentResultIndex].setText(
                        formatYutResultWithSuperscript(result, currentText));
                // 버튼 활성화 상태 유지
                randomYutBtn.setEnabled(true);
                return;
            }

            // 다른 결과가 있으면 다음 칸으로 이동
            currentResultIndex++;

            // 모든 칸이 찼는지 확인
            if (currentResultIndex >= resultLabels.length) {
                resetResults();
                resultLabels[0].setText(result);
                currentResultIndex = 0;
            } else {
                resultLabels[currentResultIndex].setText(result);
            }

            // 버튼 활성화 상태 유지
            randomYutBtn.setEnabled(true);
        } else {
            // 도, 개, 걸, 빽도 등 다른 결과일 경우
            // 현재 칸이 비어있으면 현재 칸에 표시
            if (resultLabels[currentResultIndex].getText().equals("-")) {
                resultLabels[currentResultIndex].setText(result);
            } else {
                currentResultIndex++;

                if (currentResultIndex >= resultLabels.length) {
                    resetResults();
                    resultLabels[0].setText(result);
                    currentResultIndex = 0;
                } else {
                    resultLabels[currentResultIndex].setText(result);
                }
            }

            // 윷이나 모가 아니면 버튼 비활성화
            randomYutBtn.setEnabled(false);
        }

        // 현재 윷 결과 업데이트
        updateCurrentYut(convertYutTypeToEnglish(result));
    }

    @Override
    public void updateCurrentYut(String yutType) {
        currentYutLabel.setText(convertYutTypeToKorean(yutType));
        updateYutSticks(yutType);
    }

    @Override
    public void updateYutSticks(String yutType) {
        // 모든 윷 스틱 앞면으로 초기화
        for (JLabel stick : yutSticks) {
            stick.setIcon(upIcon);
        }

        Random random = new Random();

        switch (yutType) {
            case "DO": // 도 (1개 뒤집힘)
                flipRandomSticks(1, random);
                break;
            case "GAE": // 개 (2개 뒤집힘)
                flipRandomSticks(2, random);
                break;
            case "GEOL": // 걸 (3개 뒤집힘)
                flipRandomSticks(3, random);
                break;
            case "YUT": // 윷 (4개 모두 뒷면)
                for (JLabel stick : yutSticks) {
                    stick.setIcon(downIcon);
                }
                break;
            case "MO": // 모 (4개 모두 앞면)
                // 초기화된 상태와 동일
                break;
            case "BACK_DO": // 빽도 (1개 뒤집힘, 특별 처리 필요시)
                int backDoIndex = random.nextInt(4);
                yutSticks[backDoIndex].setIcon(backDoDownIcon);
                break;
        }
    }

    // 랜덤으로 윷 스틱 뒤집는 로직 추가
    private void flipRandomSticks(int count, Random random) {
        boolean[] flipped = new boolean[4];
        int flippedCount = 0;

        while (flippedCount < count) {
            int idx = random.nextInt(4);
            if (!flipped[idx]) {
                yutSticks[idx].setIcon(downIcon);
                flipped[idx] = true;
                flippedCount++;
            }
        }
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