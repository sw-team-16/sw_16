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
import com.sw.yutnori.dto.game.response.TurnInfoResponse;
import com.sw.yutnori.dto.piece.response.PieceInfoResponse;
import com.sw.yutnori.ui.PiecePositionDisplayManager;
import com.sw.yutnori.ui.SwingYutBoardPanel;
import com.sw.yutnori.ui.SwingYutControlPanel;
import com.sw.yutnori.ui.SwingStatusPanel;
import com.sw.yutnori.ui.display.GameSetupDisplay;

import javax.swing.*;
import java.util.ArrayList;
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
    private YutResult pendingRandomYutResult = null;
    private Long pendingRandomTurnId = null;
    // 윷 던지기 결과 저장용 리스트 (프론트에서만 관리, 한 턴에 던진 모든 윷 결과)
    private final List<String> yutThrowResults = new ArrayList<>();

    public InGameController(BoardModel boardModel, GameApiClient apiClient, GameSetupDisplay.SetupData setupData) {
        this.boardModel = boardModel;
        this.apiClient = apiClient;
        this.setupData = setupData;
        this.yutBoardPanel = new SwingYutBoardPanel(boardModel);
        this.controlPanel = new SwingYutControlPanel(apiClient, this);
        this.statusPanel = new SwingStatusPanel(setupData.players(), setupData.pieceCount());
        this.yutBoardPanel.setInGameController(this);
        this.displayManager = new PiecePositionDisplayManager(boardModel, yutBoardPanel);
    }


    public void setGameContext(Long gameId, Long playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.controlPanel.setGameContext(gameId, playerId);
    }

    public void setPlayerPieceMap(Map<Long, List<Long>> map) {
        this.playerPieceMap = map;
    }

    // '랜덤 윷 던지기'를 클릭했을 때 발생하는 이벤트
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

            // 윷 던지기 결과 리스트에 추가
            yutThrowResults.add(result);
            System.out.println("[이번 턴 윷 던지기 리스트] " + yutThrowResults);

            String koreanResult = controlPanel.getResultDisplay().convertYutTypeToKorean(result);
            controlPanel.updateYutResult(koreanResult, result);

            // 윷이나 모가 나왔을 경우에는 버튼을 활성화 상태로 유지
            if (yutResult != YutResult.YUT &&
                yutResult != YutResult.MO) {
                controlPanel.enableRandomButton(false);
                
            }

            // 랜덤 윷 결과 저장 및 말 선택 유도
            this.pendingRandomYutResult = yutResult;
            this.pendingRandomTurnId = getCurrentTurnId();
            promptPieceSelection(playerId); // 말 선택 창 띄우기
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

            // 지정 윷 던지기 시 리스트를 새로 저장
            yutThrowResults.clear();
            yutThrowResults.addAll(selectedYuts);
            System.out.println("[이번 턴 윷 던지기 리스트] " + yutThrowResults);

            Long turnId = getCurrentTurnId();
            Long pieceId = getSelectedPieceId();
            if (pieceId == null) {
                controlPanel.showError("말이 선택되지 않았습니다.");
                return;
            }

            LogicalPosition start;
            if (piecePrevPositionMap.containsKey(pieceId)) {
                start = piecePrevPositionMap.get(pieceId);
            } else {
                PieceInfoResponse pieceInfo = pieceApiClient.getPieceInfo(pieceId);
                start = new LogicalPosition(pieceId, pieceInfo.getA(), pieceInfo.getB());
                piecePrevPositionMap.put(pieceId, start);
            }

            BoardType boardType = parseBoardType(setupData.boardType());
            LogicalPosition current = start;

            // 모든 윷 처리
            for (String selectedYut : selectedYuts) {
                String yutType = controlPanel.getResultDisplay().convertYutTypeToEnglish(selectedYut);
                YutResult result = convertStringToYutResult(yutType);

                apiClient.throwYutManual(gameId, turnId, playerId, pieceId, result);
                controlPanel.updateYutResult(selectedYut, yutType);

                current = BoardPathManager.calculateDestination(
                        pieceId, current.getA(), current.getB(),
                        start.getA(), start.getB(), result, boardType
                );
            }

            // 마지막 윷만 표시
            String lastYutType = controlPanel.getResultDisplay().convertYutTypeToEnglish(
                    selectedYuts.get(selectedYuts.size() - 1)
            );
            controlPanel.updateCurrentYut(lastYutType);
            YutResult lastResult = convertStringToYutResult(lastYutType);

            MovePieceRequest moveRequest = new MovePieceRequest();
            moveRequest.setPlayerId(playerId);
            moveRequest.setChosenPieceId(pieceId);
            moveRequest.setMoveOrder(1);
            moveRequest.setA(current.getA());
            moveRequest.setB(current.getB());
            moveRequest.setResult(lastResult);

            pieceApiClient.movePiece(gameId, moveRequest);

            try {
                PieceInfoResponse updatedPieceInfo = pieceApiClient.getPieceInfo(pieceId);
                LogicalPosition newPos = new LogicalPosition(pieceId, updatedPieceInfo.getA(), updatedPieceInfo.getB());
                displayManager.showLogicalPosition(newPos, pieceId);
            } catch (Exception e) {
                controlPanel.showError("말 위치 표시 중 오류 발생: " + e.getMessage());
            }

            TurnInfoResponse turnInfo = apiClient.getTurnInfo(gameId);
            this.playerId = turnInfo.getPlayerId();
            this.currentTurnId = turnInfo.getTurnId();

            statusPanel.updateCurrentPlayer(turnInfo.getPlayerName());
            controlPanel.setGameContext(gameId, playerId);
            controlPanel.enableRandomButton(false);
            controlPanel.enableCustomButton(true);

        } catch (Exception ex) {
            handleError(ex);
        } finally {
            // 항상 리셋
            resetPieceSelection();
            controlPanel.restorePanel();
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
            // --- 랜덤 윷 결과가 있을 경우, 해당 결과로 이동 처리 ---
            if (pendingRandomYutResult != null) {
                movePieceWithRandomYutResult();
            }
        }
    }

    // 랜덤 윷 결과로 말 이동 처리
    private void movePieceWithRandomYutResult() {
        try {
            Long pieceId = getSelectedPieceId();
            if (pieceId == null) {
                controlPanel.showError("말이 선택되지 않았습니다.");
                return;
            }
            LogicalPosition start;
            if (piecePrevPositionMap.containsKey(pieceId)) {
                start = piecePrevPositionMap.get(pieceId);
            } else {
                PieceInfoResponse pieceInfo = pieceApiClient.getPieceInfo(pieceId);
                start = new LogicalPosition(pieceId, pieceInfo.getA(), pieceInfo.getB());
                piecePrevPositionMap.put(pieceId, start);
            }
            BoardType boardType = parseBoardType(setupData.boardType());
            LogicalPosition current = BoardPathManager.calculateDestination(
                pieceId, start.getA(), start.getB(),
                start.getA(), start.getB(), pendingRandomYutResult, boardType
            );

            MovePieceRequest moveRequest = new MovePieceRequest();
            moveRequest.setPlayerId(playerId);
            moveRequest.setChosenPieceId(pieceId);
            moveRequest.setMoveOrder(1);
            moveRequest.setA(current.getA());
            moveRequest.setB(current.getB());
            moveRequest.setResult(pendingRandomYutResult);

            pieceApiClient.movePiece(gameId, moveRequest);

            try {
                PieceInfoResponse updatedPieceInfo = pieceApiClient.getPieceInfo(pieceId);
                LogicalPosition newPos = new LogicalPosition(pieceId, updatedPieceInfo.getA(), updatedPieceInfo.getB());
                displayManager.showLogicalPosition(newPos, pieceId);
            } catch (Exception e) {
                controlPanel.showError("말 위치 표시 중 오류 발생: " + e.getMessage());
            }

            TurnInfoResponse turnInfo = apiClient.getTurnInfo(gameId);
            this.playerId = turnInfo.getPlayerId();
            this.currentTurnId = turnInfo.getTurnId();

            statusPanel.updateCurrentPlayer(turnInfo.getPlayerName());
            controlPanel.setGameContext(gameId, playerId);

            if (pendingRandomYutResult == YutResult.YUT || pendingRandomYutResult == YutResult.MO) {
                controlPanel.enableRandomButton(true);
            } else {
                controlPanel.enableRandomButton(false);
            }
            controlPanel.enableCustomButton(false); // 랜덤 윷 후 지정 윷 비활성화
        } catch (Exception ex) {
            handleError(ex);
        } finally {
            // 항상 리셋
            resetPieceSelection();
            controlPanel.restorePanel();
            // 랜덤 윷 결과 초기화
            pendingRandomYutResult = null;
            pendingRandomTurnId = null;
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
    private YutResult convertStringToYutResult(String yutType) {
        return switch (yutType) {
            case "DO" -> YutResult.DO;
            case "GAE" -> YutResult.GAE;
            case "GEOL" -> YutResult.GEOL;
            case "YUT" -> YutResult.YUT;
            case "MO" -> YutResult.MO;
            case "BACKDO" -> YutResult.BACK_DO;
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
    public void refreshTurnInfo() {
        try {
            TurnInfoResponse turnInfo = apiClient.getTurnInfo(gameId);
            Long currentTurnPlayerId = turnInfo.getPlayerId();

            updateTurnId(turnInfo.getTurnId());

            // 내 턴이면 버튼 활성화
            boolean isMyTurn = currentTurnPlayerId.equals(playerId);
            controlPanel.enableRandomButton(isMyTurn);
            controlPanel.enableCustomButton(isMyTurn);

            // 내 턴이 시작될 때 리스트 초기화
            if (isMyTurn) {
                yutThrowResults.clear();
                System.out.println("[윷 던지기 리스트 초기화] 내 턴 시작: " + yutThrowResults);
            }

            // UI 갱신
            statusPanel.updateCurrentPlayer(turnInfo.getPlayerName()); // 선택적으로
            controlPanel.startNewTurn(); // 결과 리셋

        } catch (Exception ex) {
            handleError(ex);
        }
    }

} 