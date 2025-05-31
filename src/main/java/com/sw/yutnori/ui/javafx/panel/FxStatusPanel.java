package com.sw.yutnori.ui.javafx.panel;

import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.Player;
import com.sw.yutnori.model.enums.PieceState;
import com.sw.yutnori.ui.display.GameSetupDisplay;
import com.sw.yutnori.ui.panel.StatusPanel;
import com.sw.yutnori.ui.javafx.util.FxColorUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 사용자 (말) 상태 표시 패널
public class FxStatusPanel extends HBox implements StatusPanel {
    private final Map<String, VBox> playerPanels = new HashMap<>();
    private final Map<String, Label> playerTitleLabels = new HashMap<>();
    private final Map<String, FlowPane> playerPiecePanes = new HashMap<>();

    public FxStatusPanel(List<GameSetupDisplay.PlayerInfo> players, int pieceCount) {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);
        for (GameSetupDisplay.PlayerInfo player : players) {
            VBox playerPanel = new VBox(5);
            playerPanel.setPadding(new Insets(5));
            playerPanel.setAlignment(Pos.TOP_CENTER);
            playerPanel.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
            Label titleLabel = new Label(player.name());
            playerTitleLabels.put(player.name(), titleLabel);
            playerPanel.getChildren().add(titleLabel);
            FlowPane pieceDisplayPane = new FlowPane();
            pieceDisplayPane.setHgap(4);
            pieceDisplayPane.setVgap(4);
            pieceDisplayPane.setAlignment(Pos.CENTER);
            playerPiecePanes.put(player.name(), pieceDisplayPane);
            // 초기 말 표시
            Color fxColor = FxColorUtils.parseColor(player.color());
            for (int i = 0; i < pieceCount; i++) {
                Rectangle rect = new Rectangle(20, 20, fxColor);
                rect.setStroke(Color.BLACK);
                pieceDisplayPane.getChildren().add(rect);
            }
            playerPanel.getChildren().add(pieceDisplayPane);
            playerPanels.put(player.name(), playerPanel);
            HBox.setHgrow(playerPanel, Priority.ALWAYS);
            getChildren().add(playerPanel);
        }
    }

    @Override
    public void updateCurrentPlayer(String currentPlayerName) {
        for (Map.Entry<String, VBox> entry : playerPanels.entrySet()) {
            String playerName = entry.getKey();
            VBox panel = entry.getValue();
            Label titleLabel = playerTitleLabels.get(playerName);
            if (playerName.equals(currentPlayerName)) {
                panel.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                titleLabel.setText("▶ " + playerName);
            } else {
                panel.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
                titleLabel.setText(playerName);
            }
        }
    }

    @Override
    public void updatePlayerStatus(Player player) {
        FlowPane pieceDisplayPane = playerPiecePanes.get(player.getName());
        if (pieceDisplayPane == null) return;
        pieceDisplayPane.getChildren().clear();
        Color fxColor = FxColorUtils.parseColor(player.getColor());
        long readyPieceCount = player.getPieces().stream()
                .filter(p -> p.getState() == PieceState.READY && !p.isFinished())
                .count();
        for (int i = 0; i < readyPieceCount; i++) {
            Rectangle rect = new Rectangle(20, 20, fxColor);
            rect.setStroke(Color.BLACK);
            pieceDisplayPane.getChildren().add(rect);
        }
    }

    // InGameFrame 클래스에서 사용
    public Object getMainComponent() {
        return this;
    }
}
