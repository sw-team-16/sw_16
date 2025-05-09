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
import com.sw.yutnori.ui.SwingYutBoardPanel;
import com.sw.yutnori.ui.SwingYutControlPanel;
import com.sw.yutnori.ui.SwingStatusPanel;
import com.sw.yutnori.ui.display.GameSetupDisplay;

public class InGameController {
    private final BoardModel boardModel;
    private final GameApiClient apiClient;
    private final SwingYutBoardPanel yutBoardPanel;
    private final SwingYutControlPanel controlPanel;
    private final SwingStatusPanel statusPanel;
    private final GameSetupDisplay.SetupData setupData;
    private Long gameId;
    private Long playerId;
    private Long currentTurnId = null;
    private Long selectedPieceId = null;

    public InGameController(BoardModel boardModel, GameApiClient apiClient, GameSetupDisplay.SetupData setupData) {
        this.boardModel = boardModel;
        this.apiClient = apiClient;
        this.setupData = setupData;
        this.yutBoardPanel = new SwingYutBoardPanel(boardModel);
        this.controlPanel = new SwingYutControlPanel(apiClient);
        this.statusPanel = new SwingStatusPanel(setupData.players(), setupData.pieceCount());
        
        // 게임 설정 정보 전달
    }

    public void setGameContext(Long gameId, Long playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.controlPanel.setGameContext(gameId, playerId);
    }
    // 윷 랜덤 던지기 (SwingYutControlPanel에서 여기로 이동)
    public void onRandomYutButtonClicked() {
        try {
            Long turnId = getCurrentTurnId();
            var response = apiClient.getRandomYutResult(gameId, turnId, playerId);

            if (response.getTurnId() != null) {
                updateTurnId(response.getTurnId());
            }

            var yutResult = response.getResult();
            String result = yutResult.name();

            String koreanResult = controlPanel.getResultDisplay().convertYutTypeToKorean(result);
            controlPanel.displayYutResult(koreanResult);
            controlPanel.updateCurrentYut(result);

            if (yutResult != com.sw.yutnori.common.enums.YutResult.YUT && yutResult != com.sw.yutnori.common.enums.YutResult.MO) {
                controlPanel.setRandomYutButtonEnabled(false);
            }
        } catch (Exception ex) {
            controlPanel.showError("서버 통신 오류: " + ex.getMessage());
        }
    }

    // 윷 수동 던지기 (SwingYutControlPanel에서 여기로 이동)
    public void onCustomYutButtonClicked(java.util.List<String> selectedYuts) {
        try {
            if (selectedYuts.isEmpty()) {
                controlPanel.showError("선택된 윷 결과가 없습니다.");
                controlPanel.restorePanel();
                return;
            }

            Long turnId = getCurrentTurnId();
            Long pieceId = getSelectedPieceId();

            for (String selectedYut : selectedYuts) {
                String yutType = controlPanel.getResultDisplay().convertYutTypeToEnglish(selectedYut);
                var result = convertStringToYutResult(yutType);
                apiClient.throwYutManual(gameId, turnId, playerId, pieceId, result);
                controlPanel.displayYutResult(selectedYut);
            }

            String lastYutType = controlPanel.getResultDisplay().convertYutTypeToEnglish(selectedYuts.get(selectedYuts.size() - 1));
            controlPanel.updateCurrentYut(lastYutType);

            resetPieceSelection();
            controlPanel.setRandomYutButtonEnabled(false);
            controlPanel.restorePanel();
        } catch (Exception ex) {
            controlPanel.showError("서버 통신 오류: " + ex.getMessage());
            controlPanel.restorePanel();
        }
    }

    // 게임 승자 표시 (SwingYutControlPanel에서 여기로 이동)
    public void onWinner(String winnerName) {
        controlPanel.showWinnerDialog(winnerName);
    }

    // 게임 재시작 (SwingYutControlPanel에서 여기로 이동)
    public void restartGame() {
        if (gameId != null && playerId != null) {
            apiClient.restartGame(gameId, playerId);
        }
        controlPanel.closeWindowAndOpenSetup();
    }

    // 현재 턴 조회
    private Long getCurrentTurnId() {
        if (currentTurnId == null) {
            currentTurnId = 1L;
        }
        return currentTurnId;
    }

    // 현재 턴 업데이트
    public void updateTurnId(Long turnId) {
        this.currentTurnId = turnId;
    }

    // 선택된 말 아이디 조회
    private Long getSelectedPieceId() {
        return selectedPieceId;
    }

    // 선택된 말 아이디 초기화
    public void resetPieceSelection() {
        this.selectedPieceId = null;
    }

    // 윷 타입 문자열을 윷 결과 열거형으로 변환
    private com.sw.yutnori.common.enums.YutResult convertStringToYutResult(String yutType) {
        return switch (yutType) {
            case "DO" -> com.sw.yutnori.common.enums.YutResult.DO;
            case "GAE" -> com.sw.yutnori.common.enums.YutResult.GAE;
            case "GEOL" -> com.sw.yutnori.common.enums.YutResult.GEOL;
            case "YUT" -> com.sw.yutnori.common.enums.YutResult.YUT;
            case "MO" -> com.sw.yutnori.common.enums.YutResult.MO;
            case "BACK_DO" -> com.sw.yutnori.common.enums.YutResult.BACK_DO;
            default -> throw new IllegalArgumentException("알 수 없는 윷 타입: " + yutType);
        };
    }

    public SwingYutBoardPanel getYutBoardPanel() {
        return yutBoardPanel;
    }
    public SwingYutControlPanel getControlPanel() {
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