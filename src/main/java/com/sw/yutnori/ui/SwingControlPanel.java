package com.sw.yutnori.ui;

import javax.swing.*;
import java.awt.*;

public class SwingControlPanel extends JPanel {
    public SwingControlPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(350, 700));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 70));


        // 윷 판 패널
        JPanel yutPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        yutPanel.setBorder(BorderFactory.createEmptyBorder());
        yutPanel.setMaximumSize(new Dimension(300, 180));
        yutPanel.setPreferredSize(new Dimension(300, 180));
        yutPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 윷 판 이미지 추가 필요
//        for (int i = 0; i < 4; i++) {
//            JLabel stickLabel = new JLabel();
//            ImageIcon stickIcon = new ImageIcon(getClass().getResource("/images/front.png"));
//            Image scaledImage = stickIcon.getImage().getScaledInstance(50, 100, Image.SCALE_SMOOTH);
//            stickLabel.setIcon(new ImageIcon(scaledImage));
//            yutPanel.add(stickLabel);
//        }

        // 윷 누적 결과 창 패널
        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(BorderFactory.createTitledBorder("Result"));
        resultPanel.setMaximumSize(new Dimension(300, 100));
        resultPanel.setPreferredSize(new Dimension(300, 100));
        resultPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        // 현재 윷 패널
        JPanel currentYutPanel = new JPanel();
        currentYutPanel.setBorder(BorderFactory.createTitledBorder("Current_yut"));
        currentYutPanel.setMaximumSize(new Dimension(150, 80));
        currentYutPanel.setPreferredSize(new Dimension(150, 80));
        currentYutPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        // '랜덤 윷 던지기'와 '지정 윷 던지기' 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setOpaque(false);

        JButton randomBtn = new JButton("랜덤 윷 던지기");
        randomBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        randomBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomBtn.setFocusPainted(false);
        int randomBtnWidth = 150; // 텍스트 길이에 맞게 조정
        randomBtn.setPreferredSize(new Dimension(randomBtnWidth, 45));
        randomBtn.setMaximumSize(new Dimension(randomBtnWidth, 45));

        JButton customBtn = new JButton("지정 윷 던지기");
        customBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        customBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        customBtn.setFocusPainted(false);
        int customBtnWidth = 150; // 텍스트 길이에 맞게 조정
        customBtn.setPreferredSize(new Dimension(customBtnWidth, 45));
        customBtn.setMaximumSize(new Dimension(customBtnWidth, 45));

        buttonPanel.add(randomBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(customBtn);


        // '게임 시작' 버튼 패널
        JPanel startBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startBtnPanel.setOpaque(false);
        startBtnPanel.setMaximumSize(new Dimension(300, 150));
        startBtnPanel.setPreferredSize(new Dimension(300, 150));
        startBtnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtnPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JButton startGameBtn = new JButton("게임 시작");
        startGameBtn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        int startGameBtnWidth = 100; // 텍스트 길이에 맞게 조정
        startGameBtn.setPreferredSize(new Dimension(startGameBtnWidth, 45));
        startGameBtn.setMaximumSize(new Dimension(startGameBtnWidth, 45));
        startGameBtn.setFocusPainted(false);

        startBtnPanel.add(startGameBtn);


        // 간격 조정
        add(yutPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(resultPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(currentYutPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(buttonPanel);
        add(Box.createVerticalGlue());
        add(startBtnPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
    }
}
