package com.sw.yutnori.ui;

import com.sw.yutnori.board.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class YutBoardFrame extends JFrame {
    private YutBoardPanel boardPanel;
    private String[] shapes = {"square", "pentagon", "hexagon"};
    private int currentShapeIdx = 0;
    private JPanel controlPanel;
    private JLabel shapeLabel;

    public YutBoardFrame(BoardGraph board) {
        super("윷놀이 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        boardPanel = new YutBoardPanel(board);
        add(boardPanel, BorderLayout.CENTER);

        controlPanel = new JPanel();
        JComboBox<String> shapeCombo = new JComboBox<>(shapes);

        // 도형 선택 combo box 
        // !TODO: 나중에 수정 예정
        shapeCombo.setSelectedIndex(currentShapeIdx);
        shapeLabel = new JLabel(getShapeKoreanName(shapes[currentShapeIdx]));
        controlPanel.add(new JLabel("도형 선택: "));
        controlPanel.add(shapeCombo);
        controlPanel.add(shapeLabel);
        add(controlPanel, BorderLayout.NORTH);

        shapeCombo.addActionListener(e -> {
            int selectedIdx = shapeCombo.getSelectedIndex();
            if (selectedIdx != currentShapeIdx) {
                currentShapeIdx = selectedIdx;
                BoardGraph newBoard = new BoardGraph(shapes[currentShapeIdx], 800, 600);
                remove(boardPanel);
                boardPanel = new YutBoardPanel(newBoard);
                boardPanel.setPiecePositions(Arrays.asList("corner0"));
                add(boardPanel, BorderLayout.CENTER);
                shapeLabel.setText(getShapeKoreanName(shapes[currentShapeIdx]));
                revalidate();
                repaint();
            }
        });

        setVisible(true);
    }

    public void setPiecePositions(Collection<String> nodeIds) {
        boardPanel.setPiecePositions(nodeIds);
    }

    private String getShapeKoreanName(String shape) {
        switch (shape) {
            case "square": return "사각형";
            case "pentagon": return "오각형";
            case "hexagon": return "육각형";
            default: return shape;
        }
    }

    public static void main(String[] args) {
        BoardGraph board = new BoardGraph("square", 800, 600);
        YutBoardFrame frame = new YutBoardFrame(board);
        frame.setPiecePositions(Arrays.asList("corner0"));
    }
}
