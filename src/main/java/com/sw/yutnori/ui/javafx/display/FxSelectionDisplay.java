package com.sw.yutnori.ui.javafx.display;

import com.sw.yutnori.ui.display.SelectionDisplay;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FxSelectionDisplay implements SelectionDisplay {

    private Button doBtn, gaeBtn, geolBtn, yutBtn, moBtn, backDoBtn;
    private Button cancelBtn, confirmBtn;
    private final List<String> selectedYuts = new ArrayList<>();
    private HBox selectedYutsPane;
    private Consumer<List<String>> onConfirmCallback;
    private Runnable onCancelCallback;
    private VBox mainPane;
    private Label[] resultLabels = new Label[3];

    public FxSelectionDisplay() {
        initialize();
    }

    private void initialize() {
        mainPane = new VBox(10);
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setPadding(new Insets(10));

        createSelectedYutsPanel();

        Label infoLabel = new Label("윷을 선택하세요");
        infoLabel.setFont(Font.font("System", 14));
        mainPane.getChildren().add(infoLabel);

        createYutButtons();
        createControlButtons();
    }

    private void createSelectedYutsPanel() {
        VBox containerPane = new VBox(5);
        containerPane.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("선택된 윷 결과:");
        titleLabel.setFont(Font.font("System", 14));
        containerPane.getChildren().add(titleLabel);

        selectedYutsPane = new HBox(10);
        selectedYutsPane.setAlignment(Pos.CENTER);
        selectedYutsPane.setPadding(new Insets(10));
        selectedYutsPane.setBorder(new Border(new BorderStroke(
                Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));
        selectedYutsPane.setPrefHeight(100);
        selectedYutsPane.setMinWidth(300);

        // 결과는 최대 3개까지 가능
        for (int i = 0; i < 3; i++) {
            resultLabels[i] = new Label("-");
            resultLabels[i].setFont(Font.font("System", FontWeight.BOLD, 25));
            resultLabels[i].setAlignment(Pos.CENTER);
            resultLabels[i].setPrefWidth(90);
            selectedYutsPane.getChildren().add(resultLabels[i]);
        }

        containerPane.getChildren().add(selectedYutsPane);
        mainPane.getChildren().add(containerPane);
    }

    private void createYutButtons() {
        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        HBox buttonRow1 = new HBox(5);
        HBox buttonRow2 = new HBox(5);
        HBox buttonRow3 = new HBox(5);
        buttonRow1.setAlignment(Pos.CENTER);
        buttonRow2.setAlignment(Pos.CENTER);
        buttonRow3.setAlignment(Pos.CENTER);

        doBtn = createYutButton("도");
        gaeBtn = createYutButton("개");
        geolBtn = createYutButton("걸");
        yutBtn = createYutButton("윷");
        moBtn = createYutButton("모");
        backDoBtn = createYutButton("빽도");

        buttonRow1.getChildren().addAll(doBtn, gaeBtn);
        buttonRow2.getChildren().addAll(geolBtn, yutBtn);
        buttonRow3.getChildren().addAll(moBtn, backDoBtn);

        buttonContainer.getChildren().addAll(buttonRow1, buttonRow2, buttonRow3);
        mainPane.getChildren().add(buttonContainer);
    }

    private Button createYutButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(60, 60);

        button.setOnAction(e -> {
            String yutType = text;

            // 윷놀이 규칙 적용: 도/개/걸/빽도는 단일 선택만 가능
            if (!yutType.equals("윷") && !yutType.equals("모")) {
                // 이미 다른 일반 결과가 있으면 제거
                selectedYuts.removeIf(y -> !y.equals("윷") && !y.equals("모"));

                // 이미 같은 일반 결과가 있으면 제거 (토글)
                if (selectedYuts.contains(yutType)) {
                    selectedYuts.remove(yutType);
                    button.setStyle("");
                } else {
                    selectedYuts.add(yutType);
                    button.setStyle("-fx-background-color: #6495ED;");
                }
            } else {
                // 윷이나 모는 계속 추가 가능
                selectedYuts.add(yutType);

                // 깜빡임 효과로 버튼 선택 피드백 제공
                button.setStyle("-fx-background-color: #6495ED;");
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(300), evt -> button.setStyle(""))
                );
                timeline.play();
            }

            updateSelectedYutsPanel();
            confirmBtn.setDisable(selectedYuts.isEmpty());
        });

        return button;
    }

    private void createControlButtons() {
        HBox controlPane = new HBox(20);
        controlPane.setAlignment(Pos.CENTER);
        controlPane.setPadding(new Insets(10, 0, 0, 0));

        cancelBtn = new Button("취소");
        cancelBtn.setPrefSize(100, 40);

        confirmBtn = new Button("완료");
        confirmBtn.setPrefSize(100, 40);
        confirmBtn.setDisable(true);  // 선택 전까지 비활성화

        cancelBtn.setOnAction(e -> {
            if (onCancelCallback != null) {
                onCancelCallback.run();
            }
        });

        confirmBtn.setOnAction(e -> {
            if (!selectedYuts.isEmpty() && onConfirmCallback != null) {
                onConfirmCallback.accept(selectedYuts);
            }
        });

        controlPane.getChildren().addAll(cancelBtn, confirmBtn);
        mainPane.getChildren().add(controlPane);
    }

    @Override
    public void updateSelectedYutsPanel() {
        // 결과를 표시할 리스트
        List<Object> displayResults = new ArrayList<>();

        // 윷/모와 나머지 결과 분리 처리
        Map<String, Integer> yutMoCount = new HashMap<>();
        List<String> otherResults = new ArrayList<>();

        // 결과 분류
        for (String yut : selectedYuts) {
            if (yut.equals("윷") || yut.equals("모")) {
                yutMoCount.put(yut, yutMoCount.getOrDefault(yut, 0) + 1);
            } else {
                if (!otherResults.contains(yut)) {
                    otherResults.add(yut);
                }
            }
        }

        // 윷과 모 처리 - JavaFX 방식으로 윗첨자 표현
        for (Map.Entry<String, Integer> entry : yutMoCount.entrySet()) {
            String yut = entry.getKey();
            int count = entry.getValue();

            if (count > 1) {
                HBox container = new HBox(0);
                container.setAlignment(Pos.CENTER);

                Text baseText = new Text(yut);
                baseText.setFont(Font.font("System", FontWeight.BOLD, 25));

                Text superText = new Text(String.valueOf(count));
                superText.setFont(Font.font("System", FontWeight.BOLD, 16));
                superText.setTranslateY(-10);

                container.getChildren().addAll(baseText, superText);
                displayResults.add(container);
            } else {
                displayResults.add(yut);
            }
        }

        // 나머지 결과 추가
        displayResults.addAll(otherResults);

        // 결과 패널 초기화
        for (Label label : resultLabels) {
            label.setText("-");
            label.setGraphic(null);
        }

        // 결과 표시 (최대 3개까지)
        for (int i = 0; i < Math.min(displayResults.size(), resultLabels.length); i++) {
            Object result = displayResults.get(i);
            if (result instanceof String) {
                resultLabels[i].setText((String) result);
            } else if (result instanceof HBox) {
                resultLabels[i].setText("");
                resultLabels[i].setGraphic((HBox) result);
            }
        }
    }

    @Override
    public void setOnConfirmCallback(Consumer<List<String>> callback) {
        this.onConfirmCallback = callback;
    }

    @Override
    public void setOnCancelCallback(Runnable callback) {
        this.onCancelCallback = callback;
    }

    @Override
    public Object getMainComponent() {
        return mainPane;
    }
}
