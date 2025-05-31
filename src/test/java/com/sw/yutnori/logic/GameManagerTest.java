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
        // mock 데이터 생성 (player1 승리)
        GameManager manager = new GameManager();
        var players = List.of(
            new com.sw.yutnori.ui.display.GameSetupDisplay.PlayerInfo("A", "RED"),
            new com.sw.yutnori.ui.display.GameSetupDisplay.PlayerInfo("B", "BLUE")
        );
        var setupData = new com.sw.yutnori.ui.display.GameSetupDisplay.SetupData("사각형", 2, 4, players);
        manager.createGameFromSetupData(setupData);
        Game game = manager.getCurrentGame();
        Player player1 = game.getPlayers().get(0);

        // 모든 말을 FINISHED 상태로 변경
        for (Piece piece : player1.getPieces()) {
            piece.setState(com.sw.yutnori.model.enums.PieceState.FINISHED);
        }
        game.setWinnerPlayer(player1);
        game.setState(com.sw.yutnori.model.enums.GameState.FINISHED);

        // 결과 확인
        assertEquals(game.getNumPieces(), player1.getFinishedCount());
        assertEquals(player1, game.getWinnerPlayer());
        assertEquals(com.sw.yutnori.model.enums.GameState.FINISHED, game.getState());
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

    // 게임 생성 테스트
    @Test
    void createGameFromSetupData_withValidSetup_shouldInitializeGameAndPlayersCorrectly() {
        GameManager manager = new GameManager();
        var players = List.of(
            new com.sw.yutnori.ui.display.GameSetupDisplay.PlayerInfo("A", "RED"),
            new com.sw.yutnori.ui.display.GameSetupDisplay.PlayerInfo("B", "BLUE")
        );
        var setupData = new com.sw.yutnori.ui.display.GameSetupDisplay.SetupData("사각형", 2, 3, players);
        manager.createGameFromSetupData(setupData);
        Game game = manager.getCurrentGame();
        assertNotNull(game);
        assertEquals(2, game.getPlayers().size());
        for (Player p : game.getPlayers()) {
            assertEquals(3, p.getPieces().size());
            for (Piece piece : p.getPieces()) {
                assertEquals(0, piece.getA());
                assertEquals(1, piece.getB());
                assertEquals("READY", piece.getState().name());
            }
        }
    }

    // 랜덤 윷 생성 테스트
    @Test
    void generateRandomYut_shouldReturnValidEnumValue() {
        GameManager manager = new GameManager();
        for (int i = 0; i < 20; i++) {
            var result = manager.generateRandomYut();
            assertNotNull(result);
            assertTrue(
                result.name().equals("DO") || result.name().equals("GAE") ||
                result.name().equals("GEOL") || result.name().equals("YUT") ||
                result.name().equals("MO") || result.name().equals("BACK_DO")
            );
        }
    }

    // 윷 결과 추가 및 조회 테스트
    @Test
    void addAndGetYutResults_shouldStoreAndReturnResultsCorrectly() {
        GameManager manager = new GameManager();
        manager.addYutResult(com.sw.yutnori.model.enums.YutResult.DO);
        manager.addYutResult(com.sw.yutnori.model.enums.YutResult.GAE);
        var results = manager.getYutResults();
        assertEquals(2, results.size());
        assertEquals(com.sw.yutnori.model.enums.YutResult.DO, results.get(0));
        assertEquals(com.sw.yutnori.model.enums.YutResult.GAE, results.get(1));
    }

    // 윷 결과 삭제 테스트
    @Test
    void deleteYutResult_shouldRemoveSpecificResult() {
        GameManager manager = new GameManager();
        manager.addYutResult(com.sw.yutnori.model.enums.YutResult.DO);
        manager.addYutResult(com.sw.yutnori.model.enums.YutResult.GAE);
        manager.deleteYutResult(com.sw.yutnori.model.enums.YutResult.DO);
        var results = manager.getYutResults();
        assertEquals(1, results.size());
        assertEquals(com.sw.yutnori.model.enums.YutResult.GAE, results.get(0));
    }

    // 윷 결과 목록 초기화 테스트
    @Test
    void clearYutResults_shouldEmptyTheResultList() {
        GameManager manager = new GameManager();
        manager.addYutResult(com.sw.yutnori.model.enums.YutResult.DO);
        manager.addYutResult(com.sw.yutnori.model.enums.YutResult.GAE);
        manager.clearYutResults();
        assertTrue(manager.getYutResults().isEmpty());
    }
} 