/*
 * YutBoardPanel.java
 * 윷판을 그리는 Panel
 *  - 배경, 연결선, 노드, 노드 그림자, 중앙/모서리 노드 강조, 시작 노드 강조
 * 
 * 
 */
package com.sw.yutnori.ui;

import com.sw.yutnori.board.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class YutBoardPanel extends JPanel {
    private final BoardModel boardModel;
    private static final int BOARD_WIDTH = 1200;
    private static final int BOARD_HEIGHT = 1000;

    public YutBoardPanel(BoardModel boardModel) {
        this.boardModel = boardModel;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelW = getWidth();
        int panelH = getHeight();
        int offsetX = (panelW - BOARD_WIDTH) / 2;
        int offsetY = (panelH - BOARD_HEIGHT) / 2;

        List<Node> boardNodes = boardModel.getNodes();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, panelW, panelH);

        // 연결선
        g2.setStroke(new BasicStroke(2f));
        for (Node node : boardNodes) {
            for (Node conn : node.getConnections()) {
                if (node.getId() < conn.getId()) {
                    g2.setColor(new Color(120, 120, 120, 80));
                    g2.drawLine(
                        (int)node.getX() + offsetX, (int)node.getY() + offsetY,
                        (int)conn.getX() + offsetX, (int)conn.getY() + offsetY
                    );
                }
            }
        }

        // 노드 그리기
        for (Node node : boardNodes) {
            int x = (int) node.getX() + offsetX;
            int y = (int) node.getY() + offsetY;
            int r = 20;
            boolean isStart = node.getA() == 0 && node.getB() == 1;
            boolean isCenter = node.getA() == 3 && node.getB() == 10;
            boolean isCorner = node.getA() == 5;

            // 그림자
            g2.setColor(new Color(80, 80, 80, 60));
            g2.fillOval(x - r/2 + 3, y - r/2 + 3, r, r);

            // 중앙/모서리 노드 강조
            if (isCenter || isCorner) {
                int outerR = 34;
                int innerR = 26;
                GradientPaint gp = new GradientPaint(x-outerR/2, y-outerR/2, new Color(180, 200, 255), x+outerR/2, y+outerR/2, new Color(120, 160, 255));
                drawNodeCircle(g2, x, y, outerR, innerR, gp, new Color(60, 80, 160), 3, new Color(60, 80, 160), 2);
            } else if (isStart) {
                int outerR = 34;
                int innerR = 26;
                GradientPaint gp = new GradientPaint(x-outerR/2, y-outerR/2, new Color(255, 220, 120), x+outerR/2, y+outerR/2, new Color(255, 180, 60));
                drawNodeCircle(g2, x, y, outerR, innerR, gp, new Color(200, 120, 0), 3, new Color(200, 120, 0), 2);
                // '시작' 텍스트 추가
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("Start", x - 12, y + 30);
            } else {
                GradientPaint gp = new GradientPaint(x-r/2, y-r/2, new Color(255,255,255), x+r/2, y+r/2, new Color(200, 210, 230));
                drawNodeCircle(g2, x, y, r, r, gp, new Color(100, 120, 160), 2, new Color(100, 120, 160), 1);
            }
        }
    }

    private void drawNodeCircle(Graphics2D g2, int x, int y, int outerR, int innerR, GradientPaint outerPaint, Color outerStroke, int outerStrokeWidth, Color innerStroke, int innerStrokeWidth) {
        // 바깥 원 (그라데이션)
        g2.setPaint(outerPaint);
        g2.fillOval(x - outerR/2, y - outerR/2, outerR, outerR);
        g2.setColor(outerStroke);
        g2.setStroke(new BasicStroke(outerStrokeWidth));
        g2.drawOval(x - outerR/2, y - outerR/2, outerR, outerR);
        // 안쪽 원
        g2.setColor(Color.WHITE);
        g2.fillOval(x - innerR/2, y - innerR/2, innerR, innerR);
        g2.setColor(innerStroke);
        g2.setStroke(new BasicStroke(innerStrokeWidth));
        g2.drawOval(x - innerR/2, y - innerR/2, innerR, innerR);
        g2.setStroke(new BasicStroke(1));
    }
}
