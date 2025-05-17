package com.sw.yutnori.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    void defaultConstructor_createsObject() {
        Board board = new Board();
        assertNotNull(board);
        // boardType, nodes 등은 null 또는 기본값일 수 있음
    }

    // square 보드 타입 초기화
    @Test
    void constructor_squareBoard_initializesFieldsAndNodes() {
        Board board = new Board("square", 800, 600);
        assertEquals("square", board.getBoardType());
        assertEquals(800, board.getWidth());
        assertEquals(600, board.getHeight());
        assertNotNull(board.getNodes());
        assertFalse(board.getNodes().isEmpty());
        assertEquals(29, board.getNodes().size());
    }

    // pentagon 보드 타입 초기화
    @Test
    void constructor_pentagonBoard_initializesFieldsAndNodes() {
        Board board = new Board("pentagon", 800, 600);
        assertEquals("pentagon", board.getBoardType());
        assertEquals(800, board.getWidth());
        assertEquals(600, board.getHeight());
        assertNotNull(board.getNodes());
        assertFalse(board.getNodes().isEmpty());
        assertEquals(36, board.getNodes().size());
    }

    // hexagon 보드 타입 초기화
    @Test
    void constructor_hexagonBoard_initializesFieldsAndNodes() {
        Board board = new Board("hexagon", 800, 600);
        assertEquals("hexagon", board.getBoardType());
        assertEquals(800, board.getWidth());
        assertEquals(600, board.getHeight());
        assertNotNull(board.getNodes());
        assertFalse(board.getNodes().isEmpty());
        assertEquals(43, board.getNodes().size());
    }

    // square 보드 타입, 0, 1 노드 찾기 (존재하는 노드)
    @Test
    void findNode_returnsCorrectNodeIfExists() {
        Board board = new Board("square", 800, 600);
        Node found = board.findNode(0, 1);
        assertNotNull(found);
        assertEquals(0, found.getA());
        assertEquals(1, found.getB());
    }

    // square 보드 타입, 99, 99 노드 찾기 (존재하지 않는 노드)
    @Test
    void findNode_returnsNullIfNodeDoesNotExist() {
        Board board = new Board("square", 800, 600);
        Node found = board.findNode(99, 99);
        assertNull(found);
    }

    // square 보드 타입, 0, 1 노드 찾기 (노드 리스트가 비어있는 경우)
    @Test
    void findNode_returnsNullIfNodeListIsEmpty() {
        Board board = new Board();
        Node found = board.findNode(0, 1);
        assertNull(found);
    }

    // board id와 game 설정
    @Test
    void gettersAndSetters_idAndGame() {
        Board board = new Board();
        board.setId(123L);
        Game game = new Game();
        board.setGame(game);
        assertEquals(123L, board.getId());
        assertEquals(game, board.getGame());
    }
} 
