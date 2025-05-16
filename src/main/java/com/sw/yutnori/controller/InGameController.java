// InGameController.java - GameManager 기반으로 수정된 버전
package com.sw.yutnori.controller;

import com.sw.yutnori.board.BoardModel;
import com.sw.yutnori.logic.BoardPathManager;
import com.sw.yutnori.common.LogicalPosition;
import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.enums.BoardType;
import com.sw.yutnori.model.enums.YutResult;
import com.sw.yutnori.ui.swing.PiecePositionDisplayManager;
import com.sw.yutnori.ui.swing.panel.SwingYutBoardPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutControlPanel;
import com.sw.yutnori.ui.swing.panel.SwingStatusPanel;
import com.sw.yutnori.ui.display.GameSetupDisplay;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InGameController {
    private final BoardModel boardModel;
    private final GameManager gameManager;
    private final SwingYutBoardPanel yutBoardPanel;
    private final SwingYutControlPanel yutControlPanel;
    private final SwingStatusPanel statusPanel;
    private final GameSetupDisplay.SetupData setupData;
    private final PiecePositionDisplayManager displayManager;

    private Long playerId;
    private Long selectedPieceId = null;
    private final Map<Long, LogicalPosition> piecePrevPositionMap = new HashMap<>();
    private YutResult pendingRandomYutResult = null;

    public InGameController(BoardModel boardModel, GameManager gameManager, GameSetupDisplay.SetupData setupData) {
        this.boardModel = boardModel;
        this.gameManager = gameManager;
        this.setupData = setupData;
        this.yutBoardPanel = new SwingYutBoardPanel(boardModel);
        this.yutControlPanel = new SwingYutControlPanel(this);
        this.statusPanel = new SwingStatusPanel(setupData.players(), setupData.pieceCount());
        this.yutBoardPanel.setInGameController(this);
        this.displayManager = new PiecePositionDisplayManager(boardModel, yutBoardPanel, gameManager);
    }

    public void setGameContext(Long playerId) {
        this.playerId = playerId;
        this.yutControlPanel.setGameContext(playerId);
        this.statusPanel.updateCurrentPlayer(gameManager.getPlayer(playerId).getName());
    }

    public void onRandomYutButtonClicked() {
        try {
            YutResult result = gameManager.generateRandomYut();
            pendingRandomYutResult = result;

            String korean = yutControlPanel.getResultDisplay().convertYutTypeToKorean(result.name());
            yutControlPanel.updateYutResult(korean, result.name());

            if (result != YutResult.YUT && result != YutResult.MO) {
                yutControlPanel.enableRandomButton(false);
            }
            yutControlPanel.enableCustomButton(false);

            promptPieceSelection(playerId);

        } catch (Exception ex) {
            handleError(ex);
        }
    }

    public void onConfirmButtonClicked(List<String> selectedYuts) {
        try {
            if (selectedYuts.isEmpty() || selectedPieceId == null) {
                yutControlPanel.showErrorAndRestore("선택된 윷 결과 또는 말이 없습니다.");
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
                String eng = yutControlPanel.getResultDisplay().convertYutTypeToEnglish(selectedYut);
                YutResult result = convertStringToYutResult(eng);
                yutControlPanel.updateYutResult(selectedYut, eng);

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
                    yutControlPanel.getResultDisplay().convertYutTypeToEnglish(
                            selectedYuts.get(selectedYuts.size() - 1)
                    )
            );

            var moveResult = gameManager.movePiece(selectedPieceId, finalResult);

            // 항상 윷판 전체 새로고침
            yutBoardPanel.refreshAllPieceMarkers(gameManager.getCurrentGame().getPlayers());

            if (moveResult.groupingOccurred()) {
                JOptionPane.showMessageDialog(null, moveResult.targetPieceIds().size() + "개의 말을 업었습니다.", "업기", JOptionPane.INFORMATION_MESSAGE);
            }
            if (moveResult.captureOccurred()) { //말을 잡을 경우 랜더링을 다시 해줘야 상대방의 말이 판에서 사라짐
                JOptionPane.showMessageDialog(null, "상대 말을 잡았습니다!", "잡기", JOptionPane.INFORMATION_MESSAGE);
            }

            if (!moveResult.requiresAnotherMove()) {
                gameManager.nextTurn(playerId);
                Long nextPlayerId = gameManager.getCurrentGame().getCurrentTurnPlayer().getId();
                setGameContext(nextPlayerId);
                yutControlPanel.startNewTurn();
            } else {
                yutControlPanel.enableRandomButton(true);
            }

        } catch (Exception ex) {
            handleError(ex);
        } finally {
            resetPieceSelection();
            yutControlPanel.restorePanel();
        }
    }

    public void promptPieceSelection(Long playerId) {
        var player = gameManager.getPlayer(playerId);
        if (player == null) {
            yutControlPanel.showError("플레이어 정보를 찾을 수 없습니다.");
            return;
        }
        List<Piece> pieces = player.getPieces();
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
}
