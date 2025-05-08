/*
 * InGameController.java
 * 윷놀이 게임 컨트롤러 클래스
 *  - 게임 상태 관리
 *  - 윷판 패널 관리
 *  - 컨트롤 패널 관리
 *  - 상태 패널 관리
 */
package com.sw.yutnori.controller;

import com.sw.yutnori.board.BoardModel;
import com.sw.yutnori.client.GameApiClient;
import com.sw.yutnori.ui.YutBoardPanel;
import com.sw.yutnori.ui.SwingControlPanel;
import com.sw.yutnori.ui.SwingStatusPanel;

public class InGameController {
    private final BoardModel boardModel;
    private final GameApiClient apiClient;
    private final YutBoardPanel yutBoardPanel;
    private final SwingControlPanel controlPanel;
    private final SwingStatusPanel statusPanel;

    public InGameController(BoardModel boardModel, GameApiClient apiClient) {
        this.boardModel = boardModel;
        this.apiClient = apiClient;
        this.yutBoardPanel = new YutBoardPanel(boardModel);
        this.controlPanel = new SwingControlPanel(apiClient);
        this.statusPanel = new SwingStatusPanel();
        this.controlPanel.setGameContext(1L, 1L); // 예시: 실제 게임/플레이어 ID로 대체
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
} 