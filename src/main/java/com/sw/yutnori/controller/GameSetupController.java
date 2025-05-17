// GameSetupController.java - GameManager 기반으로 수정된 버전
package com.sw.yutnori.controller;

import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.model.Player;
import com.sw.yutnori.ui.display.GameSetupDisplay;

import java.util.*;
import java.util.function.Consumer;

public class GameSetupController {
    private InGameController inGameController;
    private GameSetupDisplay.SetupData lastSetupData;
    private final Consumer<InGameController> onGameStartCallback;
    private Consumer<Result> resultCallback;

    public GameSetupController(Consumer<InGameController> onGameStartCallback) {
        this.onGameStartCallback = onGameStartCallback;
    }

    public void setResultCallback(Consumer<Result> callback) {
        this.resultCallback = callback;
    }

    public void handleGameSetup(GameSetupDisplay.SetupData data) {
        List<GameSetupDisplay.PlayerInfo> players = data.players();
        for (GameSetupDisplay.PlayerInfo p : players) {
            if (p.name() == null || p.name().isEmpty() || p.color() == null || p.color().isEmpty()) {
                if (resultCallback != null)
                    resultCallback.accept(new Result(false, "플레이어 정보가 누락되었습니다."));
                return;
            }
        }

        Set<String> names = new HashSet<>();
        Set<String> colors = new HashSet<>();
        for (GameSetupDisplay.PlayerInfo p : players) {
            if (!names.add(p.name())) {
                if (resultCallback != null)
                    resultCallback.accept(new Result(false, "플레이어 간 이름이 중복됩니다: '" + p.name() + "'"));
                return;
            }
            if (!colors.add(p.color())) {
                if (resultCallback != null)
                    resultCallback.accept(new Result(false, "플레이어 간 색상이 중복됩니다: '" + p.color() + "'"));
                return;
            }
        }

        this.lastSetupData = data;
        this.inGameController = createInGameController(data);

        if (resultCallback != null)
            resultCallback.accept(new Result(true, "게임이 시작되었습니다!"));
        if (onGameStartCallback != null)
            onGameStartCallback.accept(this.inGameController);
    }

    private InGameController createInGameController(GameSetupDisplay.SetupData data) {
        int frameWidth = 1600;
        int frameHeight = 1100;
        int controlPanelWidth = 350;
        int statusPanelHeight = 100;
        int boardPanelWidth = frameWidth - controlPanelWidth;
        int boardPanelHeight = frameHeight - statusPanelHeight;

        String boardType = switch (data.boardType()) {
            case "오각형" -> "pentagon";
            case "육각형" -> "hexagon";
            default -> "square";
        };

        Board model = new Board(boardType, boardPanelWidth, boardPanelHeight);
        GameManager gameManager = new GameManager();

        // 게임 초기화
        gameManager.createGameFromSetupData(data);

        InGameController controller = new InGameController(model, gameManager, data);

        List<Player> players = gameManager.getCurrentGame().getPlayers();
        if (!players.isEmpty()) {
            Long playerId = players.get(0).getId();
            controller.setGameContext(playerId);
            controller.getYutBoardPanel().renderPieceObjects(playerId, players.get(0).getPieces());
        }

        return controller;
    }

    public InGameController getInGameController() {
        return inGameController;
    }

    public record Result(boolean success, String message) {
    }
}