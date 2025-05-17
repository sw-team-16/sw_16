package com.sw.yutnori.ui;

import com.sw.yutnori.model.Board;
import com.sw.yutnori.model.Node;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.Player;
import com.sw.yutnori.model.enums.PieceState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import com.sw.yutnori.ui.swing.panel.SwingYutBoardPanel;

class SwingYutBoardPanelTest {
    private Board board;
    private SwingYutBoardPanel boardPanel;
    private Node node1, node2;

    @BeforeEach
    void setUp() {
        // 노드 생성
        node1 = new Node(0, 1, 100, 100);
        node2 = new Node(1, 1, 200, 200);
        List<Node> nodes = new ArrayList<>();
        nodes.add(node1);
        nodes.add(node2);
        board = new Board("pentagon", 300, 300);
        boardPanel = new SwingYutBoardPanel(board);
        // 실제 윷판 Board 크기로 설정
        boardPanel.setSize(1200, 1000);
    }

    @Test
    void testOnlyOnBoardAndNotFinishedPiecesAreRendered() throws Exception {
        Player player = new Player();
        player.setColor("RED");
        List<Piece> pieces = new ArrayList<>();
        // ON_BOARD, not finished의 경우
        Piece p1 = new Piece();
        p1.setPieceId(1L); p1.setA(0); p1.setB(1); p1.setState(PieceState.ON_BOARD); p1.setFinished(false);
        // ON_BOARD, finished의 경우
        Piece p2 = new Piece();
        p2.setPieceId(2L); p2.setA(1); p2.setB(1); p2.setState(PieceState.ON_BOARD); p2.setFinished(true);
        // READY의 경우
        Piece p3 = new Piece();
        p3.setPieceId(3L); p3.setA(0); p3.setB(1); p3.setState(PieceState.READY); p3.setFinished(false);
        pieces.add(p1); pieces.add(p2); pieces.add(p3);
        player.setPieces(pieces);
        List<Player> players = List.of(player);

        SwingUtilities.invokeAndWait(() -> boardPanel.refreshAllPieceMarkers(players));
        // p1만 렌더링되어야 함
        assertEquals(1, boardPanel.getComponentCount());
        JButton btn = (JButton) boardPanel.getComponent(0);
        assertEquals("1", btn.getText());
    }

    @Test
    void testPieceRenderedAtCorrectNodeCenter() throws Exception {
        Player player = new Player();
        player.setColor("BLUE");
        Piece p1 = new Piece();
        p1.setPieceId(1L); p1.setA(1); p1.setB(1); p1.setState(PieceState.ON_BOARD); p1.setFinished(false);
        player.setPieces(List.of(p1));
        List<Player> players = List.of(player);

        SwingUtilities.invokeAndWait(() -> boardPanel.refreshAllPieceMarkers(players));
        JButton btn = (JButton) boardPanel.getComponent(0);
        // 노드 중심과 버튼 중앙이 일치하도록 배치
        assertEquals(200 - 15, btn.getX());
        assertEquals(200 - 15, btn.getY());
        assertEquals(30, btn.getWidth());
        assertEquals(30, btn.getHeight());
    }

    static class BoardStub extends Board {
    
        private final List<Node> nodes;
        private final int width;
        private final int height;
        public BoardStub(List<Node> nodes, int width, int height) {
            super("TEST", width, height);
            this.nodes = nodes;
            this.width = width;
            this.height = height;
        }
        @Override public List<Node> getNodes() { return nodes; }
        @Override public Node findNode(int a, int b) {
            return nodes.stream().filter(n -> n.getA() == a && n.getB() == b).findFirst().orElse(null);
        }
        @Override public int getWidth() { return width; }
        @Override public int getHeight() { return height; }
    }
} 