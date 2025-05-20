// InGameController.java - GameManager 기반으로 수정된 버전
package com.sw.yutnori.controller;

import com.sw.yutnori.logic.BoardPathManager;
import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.model.LogicalPosition;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.enums.BoardType;
import com.sw.yutnori.model.enums.YutResult;
import com.sw.yutnori.ui.swing.PiecePositionDisplayManager;
import com.sw.yutnori.ui.swing.panel.SwingStatusPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutBoardPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutControlPanel;
import com.sw.yutnori.ui.display.GameSetupDisplay;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InGameController {
    private final Board boardModel;
    private final GameManager gameManager;
    private final SwingYutBoardPanel yutBoardPanel;
    private final SwingYutControlPanel yutControlPanel;
    private final SwingStatusPanel statusPanel;
    private final GameSetupDisplay.SetupData setupData;
    private final PiecePositionDisplayManager displayManager;

    private Long playerId;
    private Long selectedPieceId = null;
    private YutResult selectedYutResult = null;
    private final Map<Long, LogicalPosition> piecePrevPositionMap = new HashMap<>();

    public InGameController(Board boardModel, GameManager gameManager, GameSetupDisplay.SetupData setupData) {
        this.boardModel = boardModel;
        this.gameManager = gameManager;
        this.setupData = setupData;
        this.yutBoardPanel = new SwingYutBoardPanel(boardModel);
        this.yutBoardPanel.setGameManager(gameManager);
        this.yutControlPanel = new SwingYutControlPanel(this);
        this.statusPanel = new SwingStatusPanel(setupData.players(), setupData.pieceCount(), gameManager);
        this.yutBoardPanel.setInGameController(this);
        this.displayManager = new PiecePositionDisplayManager(boardModel, yutBoardPanel, gameManager);
    }

    public void setGameContext(Long playerId) {
        this.playerId = playerId;
        this.statusPanel.updateCurrentPlayer(gameManager.getPlayer(playerId).getName());
    }

    // '랜덤 윷 던지기' 버튼 클릭 시 발생하는 이벤트
    public void onRandomYutButtonClicked() {
        try {
            // 윷이나 모가 나올 때까지 계속 윷을 던지고 그 결과를 저장
            YutResult result = gameManager.generateRandomYut();
            gameManager.addYutResult(result);
            yutControlPanel.updateDisplay(result.name());
            yutControlPanel.getResultDisplay().syncWithYutResults(gameManager.getYutResults());


            // 윷 던지기 완료
            if (result != YutResult.YUT && result != YutResult.MO) {
                yutControlPanel.enableRandomButton(false);
                yutControlPanel.enableCustomButton(false);

                processTurn();
            }
        } catch (Exception ex) {
            handleError(ex);
        }
    }

    // '지정 윷 던지기' 패널에서 윷 선택 완료 후 발생하는 이벤트
    public void onConfirmButtonClicked(List<String> selectedYuts) {
        try {
            // 사용자가 선택한 각 윷에 대해 처리
            for (String selectedYut : selectedYuts) {
                String engResult = yutControlPanel.getResultDisplay().convertYutTypeToEnglish(selectedYut);
                YutResult yutResult = convertStringToYutResult(engResult);

                gameManager.addYutResult(yutResult);
                System.out.println("윷 추가: " + yutResult + ", 현재 저장된 윷: " + gameManager.getYutResults().size());
            }

            yutControlPanel.getResultDisplay().syncWithYutResults(gameManager.getYutResults());
            System.out.println("저장 완료 후 윷 결과: " + gameManager.getYutResults());

            processTurn();
        } catch (Exception ex) {
            handleError(ex);
        }
    }

    // onConfirmButtonClicked()에서 말 이동 및 턴 처리 로직 분리
    private void processTurn() {
        try {
            while (!gameManager.getYutResults().isEmpty()) {
                selectedYutResult = gameManager.getYutResults().get(0); // 항상 첫 윷 결과 사용

                // 빽도 예외 처리
                if (selectedYutResult == YutResult.BACK_DO) {
                    var player = gameManager.getPlayer(playerId);

                    if (gameManager.isBackDoTurnSkippable(player)) {
                        gameManager.deleteYutResult(selectedYutResult);
                        JOptionPane.showMessageDialog(null, "OnBoard 상태의 말이 없어 턴을 넘깁니다.", "빽도", JOptionPane.INFORMATION_MESSAGE);
                        gameManager.nextTurn(playerId);
                        Long nextPlayerId = gameManager.getCurrentGame().getCurrentTurnPlayer().getId();
                        setGameContext(nextPlayerId);
                        yutControlPanel.startNewTurn();
                        return;
                    } else {
                        promptBackDoPieceSelection(playerId);
                        promptYutSelection(); // 선택된 빽도 삭제 포함
                    }
                } else {
                    promptPieceSelection(playerId);
                    promptYutSelection();
                }

                if (selectedPieceId == null || selectedYutResult == null) {
                    yutControlPanel.showErrorAndRestore("선택된 말 또는 윷 결과가 없습니다.");
                    return;
                }

                LogicalPosition current = piecePrevPositionMap.getOrDefault(
                        selectedPieceId,
                        new LogicalPosition(
                                selectedPieceId,
                                gameManager.getPiece(selectedPieceId).getA(),
                                gameManager.getPiece(selectedPieceId).getB())
                );

                BoardType boardType = gameManager.getCurrentGame().getBoardType();
                Piece piece = gameManager.getPiece(selectedPieceId);
                int prevA = piece.getA();
                int prevB = piece.getB();

                var moveResult = gameManager.movePiece(selectedPieceId, selectedYutResult);
                Piece pieceAfterMove = gameManager.getPiece(selectedPieceId);

                System.out.printf("[디버깅] 말 ID: %d, 최종 위치: (%d, %d)%n",
                        pieceAfterMove.getPieceId(), pieceAfterMove.getA(), pieceAfterMove.getB());

                if (moveResult.reachedEndPoint()) {
                    String playerName = pieceAfterMove.getPlayer().getName();
                    List<Piece> playerPieces = pieceAfterMove.getPlayer().getPieces();
                    int pieceNumber = -1;
                    for (int i = 0; i < playerPieces.size(); i++) {
                        if (playerPieces.get(i).getPieceId().equals(selectedPieceId)) {
                            pieceNumber = i + 1;
                            break;
                        }
                    }

                    JOptionPane.showMessageDialog(
                            null,
                            playerName + "님의 " + pieceNumber + "번 말이 도착지에 도달했습니다!",
                            "완주",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    if (checkGameFinishedAndShowWinner()) {
                        return;
                    }
                }

                statusPanel.updatePlayerStatus(pieceAfterMove.getPlayer());

                if (moveResult.captureOccurred()) {
                    for (Piece capturedPiece : moveResult.capturedPieces()) {
                        statusPanel.updatePlayerStatus(capturedPiece.getPlayer());
                    }
                    JOptionPane.showMessageDialog(null, "상대 말을 잡았습니다!", "잡기", JOptionPane.INFORMATION_MESSAGE);
                }

                yutBoardPanel.refreshAllPieceMarkers(gameManager.getCurrentGame().getPlayers());
                yutControlPanel.getResultDisplay().syncWithYutResults(gameManager.getYutResults());

                handleTurnChange(moveResult.requiresAnotherMove());
            }
        } catch (Exception ex) {
            handleError(ex);
        } finally {
            yutControlPanel.restorePanel();
        }
    }


    // 턴 처리 로직 분리
    public void handleTurnChange(boolean requiresAnotherMove) {
        if (!requiresAnotherMove) {
            gameManager.nextTurn(playerId);
            Long nextPlayerId = gameManager.getCurrentGame().getCurrentTurnPlayer().getId();
            setGameContext(nextPlayerId);
            yutControlPanel.startNewTurn();
        } else {
            yutControlPanel.enableRandomButton(true);
            yutControlPanel.enableCustomButton(true);
        }
        // 모든 플레이어의 Status UI 갱신 (필요시)
        for (com.sw.yutnori.model.Player player : gameManager.getCurrentGame().getPlayers()) {
            statusPanel.updatePlayerStatus(player);
        }
    }

    public void initializeView() {
        // 모든 플레이어의 대기 말 상태 표시
        for (com.sw.yutnori.model.Player player : gameManager.getCurrentGame().getPlayers()) {
            statusPanel.updatePlayerStatus(player);
        }
        // 보드 위의 초기 말 상태 표시
        yutBoardPanel.refreshAllPieceMarkers(gameManager.getCurrentGame().getPlayers());
        // 첫 번째 턴의 플레이어로 context 설정
        setGameContext(gameManager.getCurrentGame().getCurrentTurnPlayer().getId());
    }

    public void promptPieceSelection(Long playerId) {
        var player = gameManager.getPlayer(playerId);
        if (player == null) {
            yutControlPanel.showError("플레이어 정보를 찾을 수 없습니다.");
            return;
        }
        // READY 또는 ON_BOARD && !FINISHED인 말만 선택 옵션으로 제시
        List<Piece> pieces = player.getPieces().stream()
                .filter(p -> (p.getState() == com.sw.yutnori.model.enums.PieceState.READY ||
                              p.getState() == com.sw.yutnori.model.enums.PieceState.ON_BOARD)
                             && !p.isFinished())
                .toList();
        if (pieces.isEmpty()) {
            yutControlPanel.showError("선택 가능한 말이 없습니다.");
            return;
        }

        // index+1로 표시(말 번호는 양측 모두 1부터 n(2<=n<=5) 순서대로 표시), PieceId로 매핑
        String[] displayOptions = new String[pieces.size()];
        Long[] pieceIds = new Long[pieces.size()];
        for (int i = 0; i < pieces.size(); i++) {
            displayOptions[i] = (i + 1) + "번";
            pieceIds[i] = pieces.get(i).getPieceId();
        }

        Object selected = JOptionPane.showInputDialog(
                null,
                "[" + player.getName() + "] 사용할 말을 선택하세요",  // 🔹 수정된 부분
                "말 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                displayOptions,
                displayOptions[0]
        );

        if (selected != null) {
            int selectedIdx = java.util.Arrays.asList(displayOptions).indexOf(selected.toString());
            if (selectedIdx >= 0) {
                selectedPieceId = pieceIds[selectedIdx];
                    // yutBoardPanel.highlightSelectedPiece(selectedPieceId);
            }
        }
    }

    // 던져서 나온 윷 결과 중 이전에 선택한 말에 적용할 값 선택
    public void promptYutSelection() {
        List<YutResult> yutResults = gameManager.getYutResults();
        if (yutResults.isEmpty()) {
            yutControlPanel.showError("선택 가능한 윷 결과가 없습니다.");
            return;
        }

        String[] displayOptions =  new String[yutResults.size()];
        for (int i = 0; i < yutResults.size(); i++) {
            YutResult result = yutResults.get(i);
            displayOptions[i] = convertYutResultToKorean(result);
        }

        Object selected = JOptionPane.showInputDialog(
                null,
                "사용할 윷 결과를 선택하세요",
                "윷 결과 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                displayOptions,
                displayOptions[0]
        );

        if (selected != null) {
            int selectedIdx = java.util.Arrays.asList(displayOptions).indexOf(selected.toString());
            if (selectedIdx >= 0) {
                selectedYutResult = yutResults.get(selectedIdx);
                gameManager.deleteYutResult(selectedYutResult);
                yutControlPanel.getResultDisplay().syncWithYutResults(gameManager.getYutResults());
            }
        }
    }


    public void resetPieceSelection() {
        selectedPieceId = null;
    }

    public void setSelectedPieceId(Long pieceId) {
        this.selectedPieceId = pieceId;
    }

    private YutResult convertStringToYutResult(String yutType) {
        return switch (yutType) {
            case "DO" -> YutResult.DO;
            case "GAE" -> YutResult.GAE;
            case "GEOL" -> YutResult.GEOL;
            case "YUT" -> YutResult.YUT;
            case "MO" -> YutResult.MO;
            case "BACKDO", "BACK_DO" -> YutResult.BACK_DO;
            default -> throw new IllegalArgumentException("알 수 없는 윷 타입: " + yutType);
        };
    }

    private String convertYutResultToKorean(YutResult result) {
        return switch (result) {
            case DO -> "도";
            case GAE -> "개";
            case GEOL -> "걸";
            case YUT -> "윷";
            case MO -> "모";
            case BACK_DO -> "빽도";
        };
    }

    private void handleError(Exception ex) {
        yutControlPanel.showErrorAndRestore("게임 진행 중 오류 발생: " + ex.getMessage());
    }

    public SwingYutBoardPanel getYutBoardPanel() {
        return yutBoardPanel;
    }

    public SwingYutControlPanel getControlPanel() {
        return yutControlPanel;
    }

    public SwingStatusPanel getStatusPanel() {
        return statusPanel;
    }

    public Board getBoardModel() {
        return boardModel;
    }

    // 게임 종료 체크 및 승자 알림
    private boolean checkGameFinishedAndShowWinner() {
        var game = gameManager.getCurrentGame();
        for (var player : game.getPlayers()) {
            if (player.getFinishedCount() == game.getNumPieces()) {
                game.setWinnerPlayer(player);
                game.setState(com.sw.yutnori.model.enums.GameState.FINISHED);
                yutControlPanel.showWinnerDialog(player.getName());
                return true;
            }
        }
        return false;
    }
    public void promptBackDoPieceSelection(Long playerId) {
        var player = gameManager.getPlayer(playerId);
        if (player == null) {
            yutControlPanel.showError("플레이어 정보를 찾을 수 없습니다.");
            return;
        }

        List<Piece> pieces = gameManager.getOnBoardPieces(player);
        if (pieces.isEmpty()) {
            yutControlPanel.showError("OnBoard 상태의 말이 없습니다.");
            return;
        }

        String[] displayOptions = new String[pieces.size()];
        Long[] pieceIds = new Long[pieces.size()];
        for (int i = 0; i < pieces.size(); i++) {
            displayOptions[i] = (i + 1) + "번";
            pieceIds[i] = pieces.get(i).getPieceId();
        }

        Object selected = JOptionPane.showInputDialog(
                null,
                "[" + player.getName() + "] 빽도 적용할 말을 선택하세요",
                "빽도 말 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                displayOptions,
                displayOptions[0]
        );

        if (selected != null) {
            int selectedIdx = java.util.Arrays.asList(displayOptions).indexOf(selected.toString());
            if (selectedIdx >= 0) {
                selectedPieceId = pieceIds[selectedIdx];
            }
        }
    }



}
