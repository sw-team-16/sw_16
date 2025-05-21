package com.sw.yutnori.ui.swing.display;

import com.sw.yutnori.ui.display.SelectionDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SwingSelectionDisplay implements SelectionDisplay {

    private JButton doBtn, gaeBtn, geolBtn, yutBtn, moBtn, backDoBtn;
    private JButton cancelBtn, confirmBtn;
    private final List<String> selectedYuts = new ArrayList<>();
    private JPanel selectedYutsPanel;
    private Consumer<List<String>> onConfirmCallback;
    private Runnable onCancelCallback;
    private JPanel mainPanel;

    public SwingSelectionDisplay() {
        initialize();
    }

    private void initialize() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        createSelectedYutsPanel();
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel infoLabel = new JLabel("윷을 선택하세요");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        createYutButtons();
        createControlButtons();
    }

    // 선택된 윷 결과를 표시할 패널 생성
    private void createSelectedYutsPanel() {
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("선택된 윷 결과:");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPanel.add(titleLabel);

        selectedYutsPanel = new JPanel(new GridLayout(1, 3));
        selectedYutsPanel.setBorder(BorderFactory.createTitledBorder("Result"));
        selectedYutsPanel.setMaximumSize(new Dimension(300, 100));
        selectedYutsPanel.setPreferredSize(new Dimension(300, 100));

        // 결과는 최대 3개까지 가능
        for (int i = 0; i < 3; i++) {
            JLabel label = new JLabel("-");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            selectedYutsPanel.add(label);
        }

        containerPanel.add(selectedYutsPanel);
        mainPanel.add(containerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
    }

    private void createYutButtons() {
        JPanel buttonContainer = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JPanel buttonRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JPanel buttonRow3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        doBtn = createYutButton("도");
        gaeBtn = createYutButton("개");
        geolBtn = createYutButton("걸");
        yutBtn = createYutButton("윷");
        moBtn = createYutButton("모");
        backDoBtn = createYutButton("빽도");

        buttonRow1.add(doBtn);
        buttonRow1.add(gaeBtn);
        buttonRow2.add(geolBtn);
        buttonRow2.add(yutBtn);
        buttonRow3.add(moBtn);
        buttonRow3.add(backDoBtn);

        buttonContainer.add(buttonRow1);
        buttonContainer.add(buttonRow2);
        buttonContainer.add(buttonRow3);

        mainPanel.add(buttonContainer);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        ActionListener yutBtnListener = e -> {
            JButton source = (JButton) e.getSource();
            String yutType = source.getText();

            // 윷놀이 규칙 적용: 도/개/걸/빽도는 단일 선택만 가능
            if (!yutType.equals("윷") && !yutType.equals("모")) {
                // 이미 다른 일반 결과가 있으면 제거
                selectedYuts.removeIf(y -> !y.equals("윷") && !y.equals("모"));

                // 이미 같은 일반 결과가 있으면 제거 (토글)
                if (selectedYuts.contains(yutType)) {
                    selectedYuts.remove(yutType);
                    source.setBackground(UIManager.getColor("Button.background"));
                } else {
                    selectedYuts.add(yutType);
                    source.setBackground(new Color(100, 149, 237));
                }
            } else {
                // 윷이나 모는 계속 추가 가능 (토글 방식 제거)
                selectedYuts.add(yutType);
                source.setBackground(new Color(100, 149, 237));

                // 깜빡임 효과로 버튼 선택 피드백 제공
                Timer timer = new Timer(300, evt ->
                        source.setBackground(UIManager.getColor("Button.background")));
                timer.setRepeats(false);
                timer.start();
            }

            updateSelectedYutsPanel();
            confirmBtn.setEnabled(!selectedYuts.isEmpty());
        };

        doBtn.addActionListener(yutBtnListener);
        gaeBtn.addActionListener(yutBtnListener);
        geolBtn.addActionListener(yutBtnListener);
        yutBtn.addActionListener(yutBtnListener);
        moBtn.addActionListener(yutBtnListener);
        backDoBtn.addActionListener(yutBtnListener);
    }

    private void createControlButtons() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        cancelBtn = new JButton("취소");
        cancelBtn.setPreferredSize(new Dimension(100, 40));

        confirmBtn = new JButton("완료");
        confirmBtn.setPreferredSize(new Dimension(100, 40));
        confirmBtn.setEnabled(false);   // 선택 전까지 비활성화

        cancelBtn.addActionListener(e -> {
            if (onCancelCallback != null) {
                onCancelCallback.run();
            }
        });

        confirmBtn.addActionListener(e -> {
            if (!selectedYuts.isEmpty() && onConfirmCallback != null) {
                onConfirmCallback.accept(selectedYuts);
            }
        });

        controlPanel.add(cancelBtn);
        controlPanel.add(confirmBtn);
        mainPanel.add(controlPanel);
    }

    private JButton createYutButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(60, 60));
        return button;
    }

    @Override
    public void updateSelectedYutsPanel() {
        // 결과를 표시할 리스트
        List<String> displayResults = new ArrayList<>();

        // 윷/모와 나머지 결과 분리 처리
        Map<String, Integer> yutMoCount = new HashMap<>();
        List<String> otherResults = new ArrayList<>();

        // 결과 분류
        for (String yut : selectedYuts) {
            if (isSpecialResult(yut)) {
                yutMoCount.put(yut, yutMoCount.getOrDefault(yut, 0) + 1);
            } else {
                if (!otherResults.contains(yut)) {
                    otherResults.add(yut);
                }
            }
        }

        // 윷과 모 처리 - HTML 태그 활용해 위첨자로 표현
        for (Map.Entry<String, Integer> entry : yutMoCount.entrySet()) {
            String yut = entry.getKey();
            int count = entry.getValue();

            if (count > 1) {
                displayResults.add("<html><span style='font-size:18pt'>" +
                        yut + "<sup>" + count + "</sup></span></html>");
            } else {
                displayResults.add(yut);
            }
        }

        // 나머지 결과 추가
        displayResults.addAll(otherResults);

        // 결과 패널 초기화
        for (Component comp : selectedYutsPanel.getComponents()) {
            ((JLabel) comp).setText("-");
        }

        // 결과 표시 (최대 3개까지)
        Component[] components = selectedYutsPanel.getComponents();
        for (int i = 0; i < Math.min(displayResults.size(), components.length); i++) {
            JLabel label = (JLabel) components[i];
            label.setText(displayResults.get(i));
        }

        selectedYutsPanel.revalidate();
        selectedYutsPanel.repaint();
    }

    @Override
    public List<String> getSelectedYuts() {
        return new ArrayList<>(selectedYuts);
    }

    @Override
    public boolean isSpecialResult(String result) {
        return result.equals("윷") || result.equals("모");
    }

    @Override
    public void setOnConfirmCallback(Consumer<List<String>> callback) {
        this.onConfirmCallback = callback;
    }

    @Override
    public void setOnCancelCallback(Runnable callback) {
        this.onCancelCallback = callback;
    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

}