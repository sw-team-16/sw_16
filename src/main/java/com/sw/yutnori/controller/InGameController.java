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
import com.sw.yutnori.ui.swing.SwingYutBoardPanel;
import com.sw.yutnori.ui.swing.SwingYutControlPanel;
import com.sw.yutnori.ui.swing.GameSetupDisplay;
import com.sw.yutnori.ui.swing.SwingStatusPanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InGameController {
    private final Board boardModel;
    private final GameManager gameManager;
    private final SwingYutBoardPanel yutBoardPanel;
    private final SwingYutControlPanel controlPanel;
    private final SwingStatusPanel statusPanel;
    private final GameSetupDisplay.SetupData setupData;
    private final PiecePositionDisplayManager displayManager;

    private Long playerId;
    private Long selectedPieceId = null;
    private final Map<Long, LogicalPosition> piecePrevPositionMap = new HashMap<>();
    private YutResult pendingRandomYutResult = null;

    public InGameController(Board boardModel, GameManager gameManager, GameSetupDisplay.SetupData setupData) {
        this.boardModel = boardModel;
        this.gameManager = gameManager;
        this.setupData = setupData;
        this.yutBoardPanel = new SwingYutBoardPanel(boardModel);
        this.controlPanel = new SwingYutControlPanel(this);
        this.statusPanel = new SwingStatusPanel(setupData.players(), setupData.pieceCount(), gameManager);
        this.yutBoardPanel.setInGameController(this);
        this.displayManager = new PiecePositionDisplayManager(boardModel, yutBoardPanel, gameManager);

    }

    public void setGameContext(Long playerId) {
        this.playerId = playerId;
        this.controlPanel.setGameContext(null, playerId);
        this.statusPanel.updateCurrentPlayer(gameManager.getPlayer(playerId).getName());
    }

    public void onRandomYutButtonClicked() {
        try {
            YutResult result = gameManager.generateRandomYut();
            pendingRandomYutResult = result;

            String korean = controlPanel.getResultDisplay().convertYutTypeToKorean(result.name());
            controlPanel.updateYutResult(korean, result.name());

            if (result != YutResult.YUT && result != YutResult.MO) {
                controlPanel.enableRandomButton(false);
            }
            controlPanel.enableCustomButton(false);

            promptPieceSelection(playerId);

        } catch (Exception ex) {
            handleError(ex);
        }
    }

    public void onConfirmButtonClicked(List<String> selectedYuts) {
        try {
            if (selectedYuts.isEmpty() || selectedPieceId == null) {
                controlPanel.showErrorAndRestore("선택된 윷 결과 또는 말이 없습니다.");
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

            for (String selectedYut : selectedYuts) {
                String eng = controlPanel.getResultDisplay().convertYutTypeToEnglish(selectedYut);
                YutResult result = convertStringToYutResult(eng);
                controlPanel.updateYutResult(selectedYut, eng);

                current = BoardPathManager.calculateDestination(
                        selectedPieceId,
                        current.getA(), current.getB(),
                        current.getA(), current.getB(),
                        result,
                        boardType
                );
                System.out.printf("[디버깅] 말 ID: %d, %s 결과 적용 후 위치: (%d, %d)%n",
                        selectedPieceId, selectedYut, current.getA(), current.getB());
            }

            YutResult finalResult = convertStringToYutResult(
                    controlPanel.getResultDisplay().convertYutTypeToEnglish(
                            selectedYuts.get(selectedYuts.size() - 1)
                    )
            );

            var moveResult = gameManager.movePiece(selectedPieceId, finalResult);

            // 이동한 말의 소유자 Status UI 갱신)
            statusPanel.updatePlayerStatus(gameManager.getPiece(selectedPieceId).getPlayer());

            // 잡기 발생 시 잡힌 말 소유자 Status UI 갱신
            if (moveResult.captureOccurred()) {
                for (Piece capturedPiece : moveResult.capturedPieces()) {
                    statusPanel.updatePlayerStatus(capturedPiece.getPlayer());
                }
                JOptionPane.showMessageDialog(null, "상대 말을 잡았습니다!", "잡기", JOptionPane.INFORMATION_MESSAGE);
            }

            // 항상 보드 UI 갱신
            yutBoardPanel.refreshAllPieceMarkers(gameManager.getCurrentGame().getPlayers());

            // // 선택 초기화 및 강조 해제
            // selectedPieceId = null;
            // yutBoardPanel.highlightSelectedPiece(null);

            // 턴 처리
            handleTurnChange(moveResult.requiresAnotherMove());

        } catch (Exception ex) {
            handleError(ex);
        } finally {
            controlPanel.restorePanel();
        }
    }

    // 턴 처리 로직 분리
    private void handleTurnChange(boolean requiresAnotherMove) {
        if (!requiresAnotherMove) {
            gameManager.nextTurn(playerId);
            Long nextPlayerId = gameManager.getCurrentGame().getCurrentTurnPlayer().getId();
            setGameContext(nextPlayerId);
            controlPanel.startNewTurn();
        } else {
            controlPanel.enableRandomButton(true);
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
            controlPanel.showError("플레이어 정보를 찾을 수 없습니다.");
            return;
        }
        // READY 또는 ON_BOARD && !FINISHED인 말만 선택 옵션으로 제시
        List<Piece> pieces = player.getPieces().stream()
                .filter(p -> (p.getState() == com.sw.yutnori.model.enums.PieceState.READY ||
                              p.getState() == com.sw.yutnori.model.enums.PieceState.ON_BOARD)
                             && !p.isFinished())
                .toList();
        if (pieces.isEmpty()) {
            controlPanel.showError("선택 가능한 말이 없습니다.");
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
                "사용할 말을 선택하세요",
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
                if (pendingRandomYutResult != null) {
                    onConfirmButtonClicked(List.of(pendingRandomYutResult.name()));
                    pendingRandomYutResult = null;
                }
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
            case "BACKDO" -> YutResult.BACK_DO;
            default -> throw new IllegalArgumentException("알 수 없는 윷 타입: " + yutType);
        };
    }

    private void handleError(Exception ex) {
        controlPanel.showErrorAndRestore("게임 진행 중 오류 발생: " + ex.getMessage());
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

    public Board getBoardModel() {
        return boardModel;
    }
}
