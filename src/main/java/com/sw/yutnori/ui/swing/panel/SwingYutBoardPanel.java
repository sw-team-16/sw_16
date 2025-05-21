/*
 * YutBoardPanel.java
 * 윷판, 말의 표시를 그리는 Panel
 *  - 배경, 연결선, 노드, 노드 그림자, 중앙/모서리 노드 강조, 시작 노드 강조
 * 
 * 
 */
package com.sw.yutnori.ui.swing.panel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.logic.util.ColorUtils;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.model.LogicalPosition;
import com.sw.yutnori.model.Node;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.Player;
import com.sw.yutnori.model.enums.PieceState;
import com.sw.yutnori.ui.swing.PiecePositionDisplayManager;

public class SwingYutBoardPanel extends JPanel {
    private final Board board;
    private static final int BOARD_WIDTH = 1200;
    private static final int BOARD_HEIGHT = 1000;
    private final Map<Long, JComponent> pieceButtons = new HashMap<>();
    private InGameController controller;
    private List<Piece> pieceList;
    private GameManager gameManager;

    public SwingYutBoardPanel(Board board) {
        this.board = board;
        setLayout(null);
        setPreferredSize(new Dimension(board.getWidth(), board.getHeight()));
    }
    public void setInGameController(InGameController controller) {
        this.controller = controller;
    }
    private Long selectedPieceId;
    private LogicalPosition currentPosition;
    private SwingYutControlPanel controlPanel; // 주입 필수


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelW = getWidth();
        int panelH = getHeight();
        int offsetX = (panelW - BOARD_WIDTH) / 2;
        int offsetY = (panelH - BOARD_HEIGHT) / 2;

        List<Node> boardNodes = board.getNodes();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, panelW, panelH);

        // 연결선
        g2.setStroke(new BasicStroke(2f));
        for (Node node : boardNodes) {
            for (Node conn : node.getConnections()) {
                // 각 연결선을 한 번만 그리기 위해 getA()와 getB() 값을 기준으로 정렬
                if (node.getA() < conn.getA() || (node.getA() == conn.getA() && node.getB() < conn.getB())) {

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
    private Rectangle getPieceBounds(Piece piece) {
        Node node = board.findNode(piece.getA(), piece.getB());
        if (node == null) return new Rectangle(); // or throw exception
        int nodeX = (int) node.getX();
        int nodeY = (int) node.getY();
        int size = 30;
        return new Rectangle(nodeX - size/2, nodeY - size/2, size, size);
    }



    public void renderPieceObjects(Long playerId, List<Piece> pieces) {
        this.pieceList = pieces;
        removeAll();
        pieceButtons.clear();
        int x = 50;
        int y = 50;
        for (Piece piece : pieces) {
            JButton pieceBtn = new JButton("말 " + piece.getPieceId());
            pieceBtn.setBounds(x, y, 80, 40);
            pieceBtn.setBackground(Color.LIGHT_GRAY);
            pieceBtn.addActionListener(e -> {
                // highlightSelectedPiece(piece.getPieceId());
                controller.setSelectedPieceId(piece.getPieceId());
            });
            pieceButtons.put(piece.getPieceId(), pieceBtn);
            add(pieceBtn);
            y += 50;
        }
        revalidate();
        repaint();
    }



    private LogicalPosition detectClickedPiece(int x, int y) {
        for (Piece piece : pieceList) {
            // piece의 (화면상 x, y 좌표)와 클릭 좌표 비교
            Rectangle bounds = getPieceBounds(piece);
            if (bounds.contains(x, y)) {
                return new LogicalPosition(piece.getPieceId(), piece.getA(), piece.getB());
            }
        }
        return null;
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

    public void renderPiecesForPlayer(Long playerId, List<Long> pieceIds) {
        removeAll();
        pieceButtons.clear();
        int x = 50;
        int y = 50;
        for (Long pieceId : pieceIds) {
            JButton pieceBtn = new JButton("말 " + pieceId);
            pieceBtn.setBounds(x, y, 80, 40);
            pieceBtn.setBackground(Color.LIGHT_GRAY);
            pieceBtn.addActionListener(e -> {
                // highlightSelectedPiece(pieceId);
                controller.setSelectedPieceId(pieceId);
            });
            pieceButtons.put(pieceId, pieceBtn);
            add(pieceBtn);
            y += 50;
        }
        revalidate();
        repaint();
    }
    // SwingYutBoardPanel.java
    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            LogicalPosition clickedPos = detectClickedPiece(e.getX(), e.getY());
            if (clickedPos != null) {
                selectedPieceId = clickedPos.getPieceId();
                currentPosition = new LogicalPosition(clickedPos.getA(), clickedPos.getB());
                controlPanel.enableYutSelection(); // 윷 선택 UI 열기
                // 논리 좌표 기반 말 위치 표시
                PiecePositionDisplayManager markerManager = new PiecePositionDisplayManager(board, this, gameManager);
                markerManager.showLogicalPosition(currentPosition, selectedPieceId);

            }
        }
    }
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void refreshAllPieceMarkers(List<Player> players) {
        removeAll(); // 모든 컴포넌트 제거(기존 마킬 제거)
        revalidate();
        repaint();
        pieceButtons.clear();

        // paintComponent와 동일하게 offsetX, offsetY 계산
        int panelW = getWidth();
        int panelH = getHeight();
        int offsetX = (panelW - BOARD_WIDTH) / 2;
        int offsetY = (panelH - BOARD_HEIGHT) / 2;
        int pieceSize = 30;

        for (Player player : players) {
            Color color = ColorUtils.parseColor(player.getColor());
            // 그룹핑된 말 그룹 표시
            List<List<Piece>> grouped = gameManager.getGroupedPieceLists(player);
            Set<Long> groupedIds = new HashSet<>();
            for (List<Piece> group : grouped) {
                if (group.isEmpty()) continue;
                Piece rep = group.get(0); // 대표
                Node node = board.findNode(rep.getA(), rep.getB());
                if (node == null) continue;
                int x = (int) node.getX() + offsetX - (pieceSize / 2);
                int y = (int) node.getY() + offsetY - (pieceSize / 2);
                String labelText = gameManager.getGroupDisplayString(group);
                JLabel pieceLabel = new JLabel(labelText, SwingConstants.CENTER);
                pieceLabel.setToolTipText(labelText);
                pieceLabel.setBounds(x, y, pieceSize, pieceSize);
                pieceLabel.setOpaque(true);
                pieceLabel.setBackground(color);
                pieceButtons.put(rep.getPieceId(), pieceLabel);
                add(pieceLabel);
                for (Piece p : group) groupedIds.add(p.getPieceId());
            }
            // 그룹이 아닌 개별 말 표시
            for (Piece piece : player.getPieces()) {
                if ((piece.getState() == PieceState.ON_BOARD && !piece.isFinished()) && !groupedIds.contains(piece.getPieceId())) {
                    Node node = board.findNode(piece.getA(), piece.getB());
                    if (node == null) continue;
                    int x = (int) node.getX() + offsetX - (pieceSize / 2);
                    int y = (int) node.getY() + offsetY - (pieceSize / 2);
                    int displayNum = player.getPieces().indexOf(piece) + 1;
                    // JButton을 사용하니 색상이 제대로 출력되지 않는 문제가 있었음. -> JLabel로 변경
                    JLabel pieceLabel = new JLabel(String.valueOf(displayNum), SwingConstants.CENTER);
                    pieceLabel.setBounds(x, y, pieceSize, pieceSize);
                    pieceLabel.setOpaque(true);
                    pieceLabel.setBackground(color);
                    pieceLabel.setForeground(Color.BLACK);
                    pieceButtons.put(piece.getPieceId(), pieceLabel);
                    add(pieceLabel);
                }
            }
        }
        revalidate();
        repaint();
    }
    // // 선택된 말 강조 표시 (JLabel용) -> 굳이 필요한지 잘 모르겠음
    // public void highlightSelectedPiece(Long selectedId) {
    //     for (Map.Entry<Long, JComponent> entry : pieceButtons.entrySet()) {
    //         if (entry.getValue() instanceof JLabel label) {
    //             if (entry.getKey().equals(selectedId)) {
    //                 label.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
    //             } else {
    //                 label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
    //             }
    //         }
    //     }
    // }
}
