/**
 * GameSetupControllerTest.java
 * 
 * 게임 시작 컨트롤러의 테스트 클래스
 * 게임 시작 화면의 유효한 입력에 대한 테스트
 * 
 * 
 * 
 */
package com.sw.yutnori.controller;

import com.sw.yutnori.ui.display.GameSetupDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.sw.yutnori.ui.swing.SwingUIFactory;

class GameSetupControllerTest {
    private Consumer<InGameController> onGameStartCallback;
    private Consumer<GameSetupController.Result> resultCallback;
    private GameSetupController controller;

    @BeforeEach
    // 테스트 전 설정
    void setUp() {
        onGameStartCallback = mock(Consumer.class);
        resultCallback = mock(Consumer.class);
        controller = new GameSetupController(onGameStartCallback, new SwingUIFactory());
        controller.setResultCallback(resultCallback);
    }

    @Test
    void testHandleGameSetup_WithValidData_ShouldStartGameAndNotifySuccess() {
        // mock 데이터 생성
        var players = List.of(
                new GameSetupDisplay.PlayerInfo("Player1", "RED"),
                new GameSetupDisplay.PlayerInfo("Player2", "BLUE")
        );
        var data = new GameSetupDisplay.SetupData("사각형", 2, 4, players);

        // 게임 시작 처리
        controller.handleGameSetup(data);

        // 게임 시작 콜백 확인
        ArgumentCaptor<InGameController> gameCaptor = ArgumentCaptor.forClass(InGameController.class);
        ArgumentCaptor<GameSetupController.Result> resultCaptor = ArgumentCaptor.forClass(GameSetupController.Result.class);
        verify(onGameStartCallback, times(1)).accept(gameCaptor.capture());
        verify(resultCallback, times(1)).accept(resultCaptor.capture());
        assertNotNull(controller.getInGameController());
        assertNotNull(gameCaptor.getValue());
        assertTrue(resultCaptor.getValue().success());
        assertEquals("게임이 시작되었습니다!", resultCaptor.getValue().message());
    }

    @Test
    void testHandleGameSetup_WithMissingPlayerName_ShouldNotifyError() {
        // mock 데이터 생성 (이름 누락)
        var players = List.of(
                new GameSetupDisplay.PlayerInfo("", "RED"),
                new GameSetupDisplay.PlayerInfo("Player2", "BLUE")
        );
        var data = new GameSetupDisplay.SetupData("사각형", 2, 4, players);

        controller.handleGameSetup(data);

        // 결과 확인
        ArgumentCaptor<GameSetupController.Result> resultCaptor = ArgumentCaptor.forClass(GameSetupController.Result.class);
        verify(resultCallback, times(1)).accept(resultCaptor.capture());
        verify(onGameStartCallback, never()).accept(any());
        assertFalse(resultCaptor.getValue().success());
        assertEquals("플레이어 정보가 누락되었습니다.", resultCaptor.getValue().message());
    }

    @Test
    void testHandleGameSetup_WithMissingPlayerColor_ShouldNotifyError() {
        // mock 데이터 생성 (색상 누락)
        var players = List.of(
                new GameSetupDisplay.PlayerInfo("Player1", null),
                new GameSetupDisplay.PlayerInfo("Player2", "BLUE")
        );
        var data = new GameSetupDisplay.SetupData("사각형", 2, 4, players);

        controller.handleGameSetup(data);

        // 결과 확인
        ArgumentCaptor<GameSetupController.Result> resultCaptor = ArgumentCaptor.forClass(GameSetupController.Result.class);
        verify(resultCallback, times(1)).accept(resultCaptor.capture());
        verify(onGameStartCallback, never()).accept(any());
        assertFalse(resultCaptor.getValue().success());
        assertEquals("플레이어 정보가 누락되었습니다.", resultCaptor.getValue().message());
    }

    @Test
    void testHandleGameSetup_WithDuplicatePlayerNames_ShouldNotifyError() {
        // mock 데이터 생성 (이름 중복)
        var players = List.of(
                new GameSetupDisplay.PlayerInfo("Player1", "RED"),
                new GameSetupDisplay.PlayerInfo("Player1", "BLUE")
        );
        var data = new GameSetupDisplay.SetupData("사각형", 2, 4, players);

        controller.handleGameSetup(data);

        // 결과 확인
        ArgumentCaptor<GameSetupController.Result> resultCaptor = ArgumentCaptor.forClass(GameSetupController.Result.class);
        verify(resultCallback, times(1)).accept(resultCaptor.capture());
        verify(onGameStartCallback, never()).accept(any());
        assertFalse(resultCaptor.getValue().success());
        assertTrue(resultCaptor.getValue().message().startsWith("플레이어 간 이름이 중복됩니다"));
        assertTrue(resultCaptor.getValue().message().contains("Player1"));
    }

    @Test
    void testHandleGameSetup_WithDuplicatePlayerColors_ShouldNotifyError() {
        // mock 데이터 생성 (색상 중복)
        var players = List.of(
                new GameSetupDisplay.PlayerInfo("Player1", "RED"),
                new GameSetupDisplay.PlayerInfo("Player2", "RED")
        );
        var data = new GameSetupDisplay.SetupData("사각형", 2, 4, players);

        controller.handleGameSetup(data);

        // 결과 확인
        ArgumentCaptor<GameSetupController.Result> resultCaptor = ArgumentCaptor.forClass(GameSetupController.Result.class);
        verify(resultCallback, times(1)).accept(resultCaptor.capture());
        verify(onGameStartCallback, never()).accept(any());
        assertFalse(resultCaptor.getValue().success());
        assertTrue(resultCaptor.getValue().message().startsWith("플레이어 간 색상이 중복됩니다"));
        assertTrue(resultCaptor.getValue().message().contains("RED"));
    }

    @Test
    void testHandleGameSetup_CreatesInGameControllerWithCorrectBoardType() {
        // mock 데이터 생성
        var players = List.of(
                new GameSetupDisplay.PlayerInfo("Player1", "RED"),
                new GameSetupDisplay.PlayerInfo("Player2", "BLUE")
        );
        var pentagonData = new GameSetupDisplay.SetupData("오각형", 2, 4, players);
        var hexagonData = new GameSetupDisplay.SetupData("육각형", 2, 4, players);
        var squareData = new GameSetupDisplay.SetupData("사각형", 2, 4, players);

        // 게임 시작 처리
        controller.handleGameSetup(pentagonData);
        InGameController pentagonController = controller.getInGameController();
        assertNotNull(pentagonController);
        assertEquals("pentagon", pentagonController.getBoardModel().getBoardType());

        controller.handleGameSetup(hexagonData);
        InGameController hexagonController = controller.getInGameController();
        assertNotNull(hexagonController);
        assertEquals("hexagon", hexagonController.getBoardModel().getBoardType());

        controller.handleGameSetup(squareData);
        InGameController squareController = controller.getInGameController();
        assertNotNull(squareController);
        assertEquals("square", squareController.getBoardModel().getBoardType());
    }

    @Test
    void testHandleGameSetup_InvokesUIFactoryAndControllerMethods() {
        //UI 패널 모킹
        var mockUiFactory = mock(com.sw.yutnori.ui.UIFactory.class);
        var mockYutBoardPanel = mock(com.sw.yutnori.ui.panel.YutBoardPanel.class);
        var mockYutControlPanel = mock(com.sw.yutnori.ui.panel.YutControlPanel.class);
        var mockStatusPanel = mock(com.sw.yutnori.ui.panel.StatusPanel.class);
        var mockDialogDisplay = mock(com.sw.yutnori.ui.display.DialogDisplay.class);

        // 실제 InGameController는 내부에서 생성되므로, YutBoardPanel 등만 Mock
        when(mockUiFactory.createYutBoardPanel(any())).thenReturn(mockYutBoardPanel);
        when(mockUiFactory.createYutControlPanel(any())).thenReturn(mockYutControlPanel);
        when(mockUiFactory.createStatusPanel(any(), anyInt())).thenReturn(mockStatusPanel);
        when(mockUiFactory.createDialogDisplay()).thenReturn(mockDialogDisplay);

        Consumer<InGameController> onGameStartCallback = mock(Consumer.class);
        Consumer<GameSetupController.Result> resultCallback = mock(Consumer.class);
        GameSetupController controller = new GameSetupController(onGameStartCallback, mockUiFactory);
        controller.setResultCallback(resultCallback);

        var players = List.of(
                new GameSetupDisplay.PlayerInfo("Player1", "RED"),
                new GameSetupDisplay.PlayerInfo("Player2", "BLUE")
        );
        var data = new GameSetupDisplay.SetupData("사각형", 2, 4, players);

        controller.handleGameSetup(data);

        // UIFactory 메서드 호출 여부 검증
        verify(mockUiFactory, times(1)).createYutBoardPanel(any());
        verify(mockUiFactory, times(1)).createYutControlPanel(any());
        verify(mockUiFactory, times(1)).createStatusPanel(any(), anyInt());
        verify(mockUiFactory, times(1)).createDialogDisplay();

        // InGameController 내부 동작 검증 (setGameContext, renderPieceObjects)
        InGameController inGameController = controller.getInGameController();
        assertNotNull(inGameController);
        // setGameContext는 내부적으로 호출되므로, 상태 패널 업데이트가 정상적으로 호출되는지로 간접 검증
        verify(mockStatusPanel, atLeastOnce()).updateCurrentPlayer(any());
        // renderPieceObjects는 첫 번째 플레이어의 말 리스트로 호출됨
        verify(mockYutBoardPanel, atLeastOnce()).renderPieceObjects(any());
    }
}
