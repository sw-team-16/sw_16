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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.yutnori.board.BoardModel;
import com.sw.yutnori.board.BoardPathManager;
import com.sw.yutnori.client.GameApiClient;
import com.sw.yutnori.client.PieceApiClient;
import com.sw.yutnori.common.LogicalPosition;
import com.sw.yutnori.common.enums.BoardType;
import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.dto.game.request.MovePieceRequest;
import com.sw.yutnori.dto.piece.response.PieceInfoResponse;
import com.sw.yutnori.ui.PiecePositionDisplayManager;
import com.sw.yutnori.ui.SwingYutBoardPanel;
import com.sw.yutnori.ui.SwingYutControlPanel;
import com.sw.yutnori.ui.SwingStatusPanel;
import com.sw.yutnori.ui.display.GameSetupDisplay;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InGameController {
    private final BoardModel boardModel;
    private final GameApiClient apiClient;
    private final SwingYutBoardPanel yutBoardPanel;
    private final SwingYutControlPanel controlPanel;
    private final SwingStatusPanel statusPanel;
    private final GameSetupDisplay.SetupData setupData;
    private final PieceApiClient pieceApiClient = new PieceApiClient();
    private final PiecePositionDisplayManager displayManager;
    private Long gameId;
    private Long playerId;
    private Long currentTurnId = null;
    private Long selectedPieceId = null;
    private Map<Long, List<Long>> playerPieceMap = new HashMap<>();
    private final Map<Long, LogicalPosition> piecePrevPositionMap = new HashMap<>();



    public InGameController(BoardModel boardModel, GameApiClient apiClient, GameSetupDisplay.SetupData setupData) {
        this.boardModel = boardModel;
        this.apiClient = apiClient;
        this.setupData = setupData;
        this.yutBoardPanel = new SwingYutBoardPanel(boardModel);
        this.controlPanel = new SwingYutControlPanel(apiClient, this);
        this.statusPanel = new SwingStatusPanel(setupData.players(), setupData.pieceCount());
        this.yutBoardPanel.setInGameController(this);

        this.displayManager = new PiecePositionDisplayManager(boardModel, yutBoardPanel);
        // 게임 설정 정보 전달
    }
    public void onConfirmButtonClicked(List<String> selectedYuts) {
        onCustomYutButtonClicked(selectedYuts);
    }


    public void setGameContext(Long gameId, Long playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.controlPanel.setGameContext(gameId, playerId);
    }
    public void setPlayerPieceMap(Map<Long, List<Long>> map) {
        this.playerPieceMap = map;
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
            controlPanel.updateYutResult(koreanResult, result);

            // 윷이나 모가 나왔을 경우에는 버튼을 활성화 상태로 유지
            if (yutResult != com.sw.yutnori.common.enums.YutResult.YUT && 
                yutResult != com.sw.yutnori.common.enums.YutResult.MO) {
                controlPanel.enableRandomButton(false);
            }
        } catch (Exception ex) {
            handleError(ex);
        }
    }

    // 윷 수동 던지기 (SwingYutControlPanel에서 여기로 이동)
    public void onCustomYutButtonClicked(List<String> selectedYuts) {
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

            YutResult result = convertStringToYutResult(lastYutType);
            BoardType boardType = parseBoardType(setupData.boardType());

// 이동 계산 전에 이전 위치 저장
            PieceInfoResponse pieceInfo = pieceApiClient.getPieceInfo(pieceId);
            LogicalPosition prevPos = new LogicalPosition(pieceId, pieceInfo.getA(), pieceInfo.getB());
            piecePrevPositionMap.put(pieceId, prevPos);

            int currentA = pieceInfo.getA();
            int currentB = pieceInfo.getB();
            int prevA = prevPos.getA();
            int prevB = prevPos.getB();

            LogicalPosition dest = BoardPathManager.calculateDestination(
                    pieceId, currentA, currentB, prevA, prevB, result, boardType
            );


// 실제 이동 요청
            MovePieceRequest moveRequest = new MovePieceRequest();
            moveRequest.setPlayerId(playerId);
            moveRequest.setChosenPieceId(pieceId);
            moveRequest.setMoveOrder(1);
            moveRequest.setA(dest.getA());
            moveRequest.setB(dest.getB());
            moveRequest.setResult(result);

            pieceApiClient.movePiece(gameId, moveRequest);

// UI 갱신
            try {
                PieceInfoResponse updatedPieceInfo = pieceApiClient.getPieceInfo(pieceId);
                LogicalPosition newPos = new LogicalPosition(pieceId, updatedPieceInfo.getA(), updatedPieceInfo.getB());
                displayManager.showLogicalPosition(newPos, pieceId);
            } catch (Exception e) {
                controlPanel.showError("말 위치 표시 중 오류 발생: " + e.getMessage());
            }


            resetPieceSelection();
            controlPanel.enableRandomButton(false);
            controlPanel.restorePanel();
        } catch (Exception ex) {
            handleError(ex);
        }
    }
    public void promptPieceSelection(Long playerId) {
        List<Long> pieces = playerPieceMap.get(playerId);
        if (pieces == null || pieces.isEmpty()) {
            controlPanel.showError("선택 가능한 말이 없습니다.");
            return;
        }

        Object selected = JOptionPane.showInputDialog(
                null,
                "사용할 말을 선택하세요",
                "말 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                pieces.toArray(),
                pieces.get(0)
        );

        if (selected != null) {
            selectedPieceId = (Long) selected;
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

    public List<Long> getPieceIdsForPlayer(Long playerId) {
        return playerPieceMap.getOrDefault(playerId, List.of());
    }

    public void setSelectedPieceId(Long pieceId) {
        this.selectedPieceId = pieceId;
    }


    private int calculateStepCount(List<String> yuts) {
        int total = 0;
        for (String y : yuts) {
            total += switch (y) {
                case "도" -> 1;
                case "개" -> 2;
                case "걸" -> 3;
                case "윷" -> 4;
                case "모" -> 5;
                case "빽도" -> -1;
                default -> 0;
            };
        }
        return total;
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
    private BoardType parseBoardType(String rawType) {
        return switch (rawType) {
            case "사각형" -> BoardType.SQUARE;
            case "오각형" -> BoardType.PENTAGON;
            case "육각형" -> BoardType.HEXAGON;
            default -> throw new IllegalArgumentException("알 수 없는 보드 타입: " + rawType);
        };
    }


} 