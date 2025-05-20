package com.sw.yutnori.controller;

import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.*;
import com.sw.yutnori.model.enums.*;
import com.sw.yutnori.ui.display.GameSetupDisplay;
import com.sw.yutnori.ui.swing.panel.SwingStatusPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutBoardPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutControlPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InGameControllerTest {

    private InGameController controller;
    private GameManager mockGameManager;
    private Board mockBoard;
    private GameSetupDisplay.SetupData mockSetupData;
    private Game mockGame;
    private Player mockPlayer;
    private Piece mockPiece;
    private YutResult result = YutResult.DO;

    @BeforeEach
    void setUp() {
        mockBoard = mock(Board.class);
        mockGameManager = mock(GameManager.class);
        mockSetupData = mock(GameSetupDisplay.SetupData.class);
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
        mockPiece = mock(Piece.class);

        when(mockSetupData.players()).thenReturn(List.of());
        when(mockSetupData.pieceCount()).thenReturn(4);
        when(mockGameManager.getCurrentGame()).thenReturn(mockGame);
        when(mockGame.getPlayers()).thenReturn(List.of(mockPlayer));
        when(mockPlayer.getName()).thenReturn("테스트유저");
        when(mockPlayer.getPieces()).thenReturn(List.of(mockPiece));
        when(mockPlayer.getId()).thenReturn(1L);
        when(mockPiece.getPieceId()).thenReturn(1L);
        when(mockPiece.getPlayer()).thenReturn(mockPlayer);
        when(mockPiece.getState()).thenReturn(PieceState.READY);
        when(mockPiece.isFinished()).thenReturn(false);

        controller = new InGameController(mockBoard, mockGameManager, mockSetupData);
    }

    @Test
    void testOnRandomYutButtonClicked_handlesNonYutResult() {
        when(mockGameManager.generateRandomYut()).thenReturn(YutResult.DO);
        when(mockGameManager.getYutResults()).thenReturn(List.of(YutResult.DO));

        assertDoesNotThrow(() -> controller.onRandomYutButtonClicked());
        verify(mockGameManager).addYutResult(YutResult.DO);
    }

    @Test
    void testPromptPieceSelection_noPlayerFound() {
        when(mockGameManager.getPlayer(99L)).thenReturn(null);
        controller.promptPieceSelection(99L);
        // UI 오류 처리 확인용: 예외 발생 없이 종료되면 OK
    }

    @Test
    void testPromptYutSelection_emptyResults() {
        when(mockGameManager.getYutResults()).thenReturn(List.of());
        controller.promptYutSelection();
        // UI 오류 처리 확인
    }

    @Test
    void testHandleTurnChange_switchesTurn() {
        Long dummyPlayerId = 1L;

        // mock player 및 연결 설정
        when(mockPlayer.getId()).thenReturn(dummyPlayerId);
        when(mockPlayer.getName()).thenReturn("테스트유저");
        when(mockGameManager.getPlayer(dummyPlayerId)).thenReturn(mockPlayer);
        when(mockGame.getCurrentTurnPlayer()).thenReturn(mockPlayer);
        when(mockGameManager.getCurrentGame()).thenReturn(mockGame);
        when(mockGame.getPlayers()).thenReturn(List.of(mockPlayer));

        controller.setGameContext(dummyPlayerId);
        controller.handleTurnChange();

        verify(mockGameManager).nextTurn(dummyPlayerId);
        verify(mockGameManager, atLeastOnce()).getPlayer(dummyPlayerId);
    }


    @Test
    void testHandleTurnChange_staysSameTurn() {
        Long dummyPlayerId = 1L;

        when(mockPlayer.getId()).thenReturn(dummyPlayerId);
        when(mockPlayer.getName()).thenReturn("테스트유저");
        when(mockGameManager.getPlayer(dummyPlayerId)).thenReturn(mockPlayer);
        when(mockGame.getCurrentTurnPlayer()).thenReturn(mockPlayer);
        when(mockGameManager.getCurrentGame()).thenReturn(mockGame);
        when(mockGame.getPlayers()).thenReturn(List.of(mockPlayer));

        controller.setGameContext(dummyPlayerId);

        // 예외 없이 정상 동작 시 성공
    }


    @Test
    void testCheckGameFinishedAndShowWinner_whenFinished() throws Exception {
        when(mockPlayer.getFinishedCount()).thenReturn(4);
        when(mockGame.getNumPieces()).thenReturn(4);

        Method method = InGameController.class.getDeclaredMethod("checkGameFinishedAndShowWinner");
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(controller);

        verify(mockGame).setWinnerPlayer(mockPlayer);
        verify(mockGame).setState(GameState.FINISHED);
        assertTrue(result);
    }

    @Test
    void testCheckGameFinishedAndShowWinner_whenNotFinished() throws Exception {
        when(mockPlayer.getFinishedCount()).thenReturn(2);
        when(mockGame.getNumPieces()).thenReturn(4);

        Method method = InGameController.class.getDeclaredMethod("checkGameFinishedAndShowWinner");
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(controller);

        assertFalse(result);
    }
}
