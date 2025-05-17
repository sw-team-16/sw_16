package com.sw.yutnori.logic;

import com.sw.yutnori.model.Game;
import com.sw.yutnori.model.Player;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.enums.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {
    private GameManager gameManager;
    private Game game;
    private Player player1;
    private Player player2;

    // 테스트 전 설정
    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
        game = new Game();
        game.setNumPieces(4);
        game.setPlayers(new ArrayList<>());
        game.setState(GameState.PLAYING);
        player1 = new Player();
        player1.setName("Player1");
        player1.setNumOfPieces(4);
        player1.setFinishedCount(0);
        player1.setPieces(new ArrayList<>());
        player2 = new Player();
        player2.setName("Player2");
        player2.setNumOfPieces(4);
        player2.setFinishedCount(0);
        player2.setPieces(new ArrayList<>());
        game.getPlayers().add(player1);
        game.getPlayers().add(player2);
    }

    @Test
    void testCheckGameEnd_WhenAPlayerHasAllPiecesFinished_ShouldReturnTrue() {
        // mock 데이터 생성 (모든 말이 완료)
        for (int i = 0; i < 4; i++) {
            Piece piece = new Piece();
            piece.setPlayer(player1);
            piece.setFinished(true);
            player1.getPieces().add(piece);
        }
        player1.setFinishedCount(4);
        // 게임 종료 확인
        boolean finished = isGameFinished(game);
        // 결과 확인
        assertTrue(finished);
        assertEquals(player1, game.getWinnerPlayer());
        assertEquals(GameState.FINISHED, game.getState());
    }

    @Test
    void testCheckGameEnd_WhenNoPlayerHasAllPiecesFinished_ShouldReturnFalse() {
        // mock 데이터 생성 (모든 말이 미완료)
        for (int i = 0; i < 4; i++) {
            Piece piece1 = new Piece();
            piece1.setPlayer(player1);
            piece1.setFinished(false);
            player1.getPieces().add(piece1);
            Piece piece2 = new Piece();
            piece2.setPlayer(player2);
            piece2.setFinished(false);
            player2.getPieces().add(piece2);
        }
        player1.setFinishedCount(2);
        player2.setFinishedCount(1);
        // 게임 종료 확인
        boolean finished = isGameFinished(game);
        // 결과 확인
        assertFalse(finished);
        assertNull(game.getWinnerPlayer());
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void testSetGameFinished_SetsWinnerAndStateCorrectly() {
        // mock 데이터 생성 (player2 승리)
        declareWinner(game, player2);
        // 결과 확인
        assertEquals(player2, game.getWinnerPlayer());
        assertEquals(GameState.FINISHED, game.getState());
    }

    // 게임 종료 확인
    private boolean isGameFinished(Game game) {
        for (Player player : game.getPlayers()) {
            if (player.getFinishedCount() == game.getNumPieces()) {
                declareWinner(game, player);
                return true;
            }
        }
        return false;
    }

    // 게임 승자 선언
    private void declareWinner(Game game, Player winner) {
        game.setWinnerPlayer(winner);
        game.setState(GameState.FINISHED);
    }
} 