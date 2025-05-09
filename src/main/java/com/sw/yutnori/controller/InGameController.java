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
import com.sw.yutnori.common.enums.YutResult;

import java.util.List;

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
        this.controlPanel = new SwingYutControlPanel(apiClient, this);
        this.statusPanel = new SwingStatusPanel(setupData.players(), setupData.pieceCount());
    }

    // 게임 설정 정보 전달
    public void setGameContext(Long gameId, Long playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.controlPanel.setGameContext(gameId, playerId);
    }

    // '랜덤 윷 던지기' 버튼 클릭 시 발생하는 이벤트
    public void onRandomYutButtonClicked() {
        try {
            Long turnId = getCurrentTurnId();
            var response = apiClient.getRandomYutResult(gameId, turnId, playerId);

            if (response.getTurnId() != null) {
                updateTurnId(response.getTurnId());
            }

            // 랜덤 윷 던지기를 클릭했을 때는 다음 턴까지 지정 윷 던지기 버튼 비활성화
            controlPanel.enableCustomButton(false);

            var yutResult = response.getResult();
            String result = yutResult.name();

            String koreanResult = controlPanel.getResultDisplay().convertYutTypeToKorean(result);
            controlPanel.updateYutResult(koreanResult, result);

            // 윷이나 모가 나왔을 경우에는 버튼을 활성화 상태로 유지
            if (yutResult != YutResult.YUT &&
                yutResult != YutResult.MO) {
                controlPanel.enableRandomButton(false);
            }
        } catch (Exception ex) {
            handleError(ex);
        }
    }

    // 지정한 윳 선택 이후 '완료' 버튼 클릭 시 발생하는 이벤트
    public void onConfirmButtonClicked(List<String> selectedYuts) {
        try {
            if (selectedYuts.isEmpty()) {
                controlPanel.showErrorAndRestore("선택된 윷 결과가 없습니다.");
                return;
            }

            Long turnId = getCurrentTurnId();
            Long pieceId = getSelectedPieceId();
            if (pieceId == null) {
                controlPanel.showError("말이 선택되지 않았습니다.");
                return;
            }

            // 모든 선택된 결과들을 백엔드로 전송
            for (String selectedYut : selectedYuts) {
                // 한국어 윷 결과를 백엔드 전송용 영어로 변환
                String yutType = controlPanel.getResultDisplay().convertYutTypeToEnglish(selectedYut);
                var result = convertStringToYutResult(yutType);
                // 백엔드 API 호출
                apiClient.throwYutManual(gameId, turnId, playerId, pieceId, result);
                controlPanel.updateYutResult(selectedYut, yutType);
            }

            // 마지막 선택 윷을 현재 윷으로 표시
            String lastYutType = controlPanel.getResultDisplay().convertYutTypeToEnglish(
                selectedYuts.get(selectedYuts.size() - 1)
            );
            controlPanel.updateCurrentYut(lastYutType);

            // 지정 윷 선택 완료되면 다음 턴까지 두 버튼 모두 비활성화
            resetPieceSelection();
            controlPanel.restorePanel();
            controlPanel.enableRandomButton(false);
            controlPanel.enableCustomButton(false);
        } catch (Exception ex) {
            handleError(ex);
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
    private YutResult convertStringToYutResult(String yutType) {
        return switch (yutType) {
            case "DO" -> YutResult.DO;
            case "GAE" -> YutResult.GAE;
            case "GEOL" -> YutResult.GEOL;
            case "YUT" -> YutResult.YUT;
            case "MO" -> YutResult.MO;
            case "BACK_DO" -> YutResult.BACK_DO;
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

    // View 업데이트 메서드들
    private void updateGameState(String result, String koreanResult) {
        controlPanel.displayYutResult(koreanResult);
        controlPanel.updateCurrentYut(result);
        controlPanel.updateYutSticks(result);
        // 보드 상태는 나중에 구현
    }

    private void updateAllViews() {
        yutBoardPanel.repaint();  // 보드 다시 그리기
        statusPanel.repaint();    // 상태 패널 업데이트
        controlPanel.revalidate();
        controlPanel.repaint();
    }

    private void handleError(Exception ex) {
        controlPanel.showErrorAndRestore("게임 진행 중 오류 발생: " + ex.getMessage());
        resetGameState();
    }

    private void resetGameState() {
        resetPieceSelection();
        controlPanel.enableRandomButton(true);
        updateAllViews();
    }
} 