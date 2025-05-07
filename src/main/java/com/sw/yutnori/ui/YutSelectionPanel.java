package com.sw.yutnori.ui;

import com.sw.yutnori.common.enums.YutResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class YutSelectionPanel extends JPanel {

    private JButton doBtn, gaeBtn, geolBtn, yutBtn, moBtn, backDoBtn;
    private JButton cancelBtn, confirmBtn;
    private String selectedYut = null;
    private Consumer<String> onConfirmCallback;
    private Runnable onCancelCallback;

    public YutSelectionPanel(Consumer<String> onConfirmCallback, Runnable onCancelCallback) {
        this.onConfirmCallback = onConfirmCallback;
        this.onCancelCallback = onCancelCallback;
        initialize();
    }

    private void initialize() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 70));

        JLabel infoLabel = new JLabel("윷을 선택하세요");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(infoLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        createYutButtons();
        createControlButtons();
    }

    private void createYutButtons() {
        // 윷 선택 버튼 패널(도~걸 / 윷~뺵도)
        JPanel buttonRows1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JPanel buttonRows2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 윷 버튼 생성
        doBtn = createYutButton("도");
        gaeBtn = createYutButton("개");
        geolBtn = createYutButton("걸");
        yutBtn = createYutButton("윷");
        moBtn = createYutButton("모");
        backDoBtn = createYutButton("빽도");

        // 첫 번째 행에 버튼 추가
        buttonRows1.add(doBtn);
        buttonRows1.add(gaeBtn);
        buttonRows1.add(geolBtn);

        // 두 번째 행에 버튼 추가
        buttonRows2.add(yutBtn);
        buttonRows2.add(moBtn);
        buttonRows2.add(backDoBtn);

        add(buttonRows1);
        add(buttonRows2);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // 윷 버튼 이벤트 리스너
        ActionListener yutBtnListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                // 모든 윷 버튼 기본 스타일로 변경
                resetButtonStyles(buttonRows1);
                resetButtonStyles(buttonRows2);

                // 선택된 버튼 강조
                source.setBackground(new Color(100, 149, 237));

                // 선택된 윷 저장
                selectedYut = source.getText();

                // 완료 버튼 활성화
                confirmBtn.setEnabled(true);
            }
        };

        // 윷 버튼에 리스너 추가
        doBtn.addActionListener(yutBtnListener);
        gaeBtn.addActionListener(yutBtnListener);
        geolBtn.addActionListener(yutBtnListener);
        yutBtn.addActionListener(yutBtnListener);
        moBtn.addActionListener(yutBtnListener);
        backDoBtn.addActionListener(yutBtnListener);
    }

    private void resetButtonStyles(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JButton) {
                c.setBackground(UIManager.getColor("Button.background"));
            }
        }
    }

    private void createControlButtons() {
        // 취소, 완료 버튼 패널
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        cancelBtn = new JButton("취소");
        cancelBtn.setPreferredSize(new Dimension(100, 40));

        confirmBtn = new JButton("완료");
        confirmBtn.setPreferredSize(new Dimension(100, 40));
        confirmBtn.setEnabled(false); // 처음에는 비활성화

        // 취소 버튼 이벤트
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onCancelCallback != null) {
                    onCancelCallback.run();
                }
            }
        });

        // 완료 버튼 이벤트
        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedYut != null && onConfirmCallback != null) {
                    onConfirmCallback.accept(selectedYut);
                }
            }
        });

        controlPanel.add(cancelBtn);
        controlPanel.add(confirmBtn);

        add(controlPanel);
    }

    private JButton createYutButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(60, 60));
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        return button;
    }

    public String getSelectedYut() {
        return selectedYut;
    }
}