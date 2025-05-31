package com.sw.yutnori.ui.javafx.display;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import com.sw.yutnori.ui.display.GameSetupDisplay;

public class FxGameSetupDisplay extends GridPane implements GameSetupDisplay {
    private Consumer<com.sw.yutnori.ui.display.GameSetupDisplay.SetupData> onStartCallback;

    private final ComboBox<String> boardTypeCombo;
    private final ComboBox<Integer> playerCountCombo;
    private final ComboBox<Integer> pieceCountCombo;
    private final VBox playersPane;
    private final List<TextField> playerNameFields = new ArrayList<>();
    private final List<ComboBox<String>> playerColorCombos = new ArrayList<>();
    private final List<String> allColors = Arrays.asList("RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "PURPLE", "BLACK", "WHITE");

    public FxGameSetupDisplay() {
        setPadding(new Insets(12, 18, 12, 18));
        setHgap(10);
        setVgap(6);
        setAlignment(javafx.geometry.Pos.TOP_CENTER);

        // 보드 타입
        Label boardTypeLabel = new Label("보드 타입:");
        boardTypeCombo = new ComboBox<>(FXCollections.observableArrayList("사각형", "오각형", "육각형"));
        boardTypeCombo.getSelectionModel().selectFirst();
        add(boardTypeLabel, 0, 0);
        add(boardTypeCombo, 1, 0);

        // 플레이어 수
        Label playerCountLabel = new Label("플레이어 수:");
        playerCountCombo = new ComboBox<>(FXCollections.observableArrayList(2, 3, 4));
        playerCountCombo.getSelectionModel().selectFirst();
        add(playerCountLabel, 0, 1);
        add(playerCountCombo, 1, 1);

        // 플레이어 정보 입력 영역
        playersPane = new VBox(3);
        GridPane.setColumnSpan(playersPane, 2);
        add(playersPane, 0, 2);

        // 말 개수
        Label pieceCountLabel = new Label("말 개수:");
        pieceCountCombo = new ComboBox<>(FXCollections.observableArrayList(2, 3, 4, 5));
        pieceCountCombo.getSelectionModel().select(Integer.valueOf(2) - 2); // 기본 2개
        add(pieceCountLabel, 0, 3);
        add(pieceCountCombo, 1, 3);

        // 게임 시작 버튼
        Button startButton = new Button("게임 시작");
        GridPane.setColumnSpan(startButton, 2);
        GridPane.setHalignment(startButton, HPos.CENTER);
        add(startButton, 0, 4);

        // 플레이어 입력 필드 초기화
        updatePlayerInputs();
        playerCountCombo.setOnAction(e -> updatePlayerInputs());

        // 게임 시작 버튼
        startButton.setOnAction(e -> {
            if (onStartCallback != null) {
                java.util.List<com.sw.yutnori.ui.display.GameSetupDisplay.PlayerInfo> players = new java.util.ArrayList<>();
                for (int i = 0; i < playerNameFields.size(); i++) {
                    String name = playerNameFields.get(i).getText();
                    String color = playerColorCombos.get(i).getValue();
                    players.add(new com.sw.yutnori.ui.display.GameSetupDisplay.PlayerInfo(name, color));
                }
                com.sw.yutnori.ui.display.GameSetupDisplay.SetupData data = new com.sw.yutnori.ui.display.GameSetupDisplay.SetupData(
                        boardTypeCombo.getValue(),
                        playerCountCombo.getValue(),
                        pieceCountCombo.getValue(),
                        players
                );
                onStartCallback.accept(data);
            }
        });
    }

    // 플레이어 입력 업데이트
    private void updatePlayerInputs() {
        playersPane.getChildren().clear();
        playerNameFields.clear();
        playerColorCombos.clear();
        int playerCount = playerCountCombo.getValue();
        for (int i = 0; i < playerCount; i++) {
            HBox playerBox = new HBox(8);
            Label nameLabel = new Label("플레이어 " + (i + 1) + " 이름:");
            TextField nameField = new TextField();
            nameField.setPrefWidth(100);
            nameField.setPromptText("Player" + (i + 1));
            if (i == 0) nameField.setText("Player1");
            else if (i == 1) nameField.setText("Player2");
            playerNameFields.add(nameField);
            Label colorLabel = new Label("색상:");
            ComboBox<String> colorCombo = new ComboBox<>();
            colorCombo.setItems(FXCollections.observableArrayList(getAvailableColors(i, null)));
            colorCombo.getSelectionModel().selectFirst();
            int idx = i;
            colorCombo.setOnAction(e -> updateColorCombos(idx));
            playerColorCombos.add(colorCombo);
            playerBox.getChildren().addAll(nameLabel, nameField, colorLabel, colorCombo);
            playersPane.getChildren().add(playerBox);
        }
    }

    // 색상 박스
    private void updateColorCombos(int changedIdx) {
        for (int i = 0; i < playerColorCombos.size(); i++) {
            ComboBox<String> combo = playerColorCombos.get(i);
            String current = combo.getValue();
            combo.setItems(FXCollections.observableArrayList(getAvailableColors(i, current)));
            if (current != null && combo.getItems().contains(current)) {
                combo.setValue(current);
            } else if (!combo.getItems().isEmpty()) {
                combo.getSelectionModel().selectFirst();
            }
        }
    }

    // 사용 가능한 색상 목록
    private List<String> getAvailableColors(int idx, String current) {
        List<String> used = new ArrayList<>();
        for (int i = 0; i < playerColorCombos.size(); i++) {
            if (i == idx) continue;
            String sel = playerColorCombos.get(i).getValue();
            if (sel != null) used.add(sel);
        }
        List<String> available = new ArrayList<>();
        for (String color : allColors) {
            if (!used.contains(color) || (current != null && color.equals(current))) {
                available.add(color);
            }
        }
        return available;
    }

    // 패널 반환
    public GridPane getPanel() {
        return this;
    }

    // 게임 시작 콜백 설정
    public void setOnStartCallback(Consumer<com.sw.yutnori.ui.display.GameSetupDisplay.SetupData> callback) {
        this.onStartCallback = callback;
    }
} 