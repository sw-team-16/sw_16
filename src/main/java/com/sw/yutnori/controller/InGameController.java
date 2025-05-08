/*
 * InGameController.java
 * 윷놀이 게임 컨트롤러 클래스
 *  - 게임 상태 관리
 *  - 윷판 패널 관리
 *  - 컨트롤 패널 관리
 *  - 상태 패널 관리
 * 
 * 
 */
package com.sw.yutnori.controller;

import com.sw.yutnori.board.BoardModel;
import com.sw.yutnori.client.GameApiClient;
import com.sw.yutnori.ui.YutBoardPanel;
import com.sw.yutnori.ui.SwingControlPanel;
import com.sw.yutnori.ui.SwingStatusPanel;
import com.sw.yutnori.ui.display.GameSetupDisplay;

public class InGameController {
    private final BoardModel boardModel;
    private final GameApiClient apiClient;
    private final YutBoardPanel yutBoardPanel;
    private final SwingControlPanel controlPanel;
    private final SwingStatusPanel statusPanel;
    private final GameSetupDisplay.SetupData setupData;

    public InGameController(BoardModel boardModel, GameApiClient apiClient, GameSetupDisplay.SetupData setupData) {
        this.boardModel = boardModel;
        this.apiClient = apiClient;
        this.setupData = setupData;
        this.yutBoardPanel = new YutBoardPanel(boardModel);
        this.controlPanel = new SwingControlPanel(apiClient);
        this.statusPanel = new SwingStatusPanel(setupData.players(), setupData.pieceCount());
        
        // 게임 설정 정보 전달
        this.controlPanel.setGameContext(1L, 1L); // 게임/플레이어 ID
    }

    public YutBoardPanel getYutBoardPanel() {
        return yutBoardPanel;
    }
    public SwingControlPanel getControlPanel() {
        return controlPanel;
    }
    public SwingStatusPanel getStatusPanel() {
        return statusPanel;
    }
    public BoardModel getBoardModel() {
        return boardModel;
    }
    public GameApiClient getApiClient() {
        return apiClient;
    }
    public GameSetupDisplay.SetupData getSetupData() {
        return setupData;
    }
} 