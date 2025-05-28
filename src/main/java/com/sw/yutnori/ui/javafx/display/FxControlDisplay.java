package com.sw.yutnori.ui.javafx.display;

import com.sw.yutnori.ui.display.ControlDisplay;
import com.sw.yutnori.ui.display.ResultDisplay;
import com.sw.yutnori.ui.display.YutDisplay;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FxControlDisplay implements ControlDisplay {

    private VBox mainPane;

    private HBox yutPane;
    private GridPane resultPane;
    private VBox currentYutPane;
    private VBox buttonPane;

    private ImageView[] yutSticks;
    private Image upImage;
    private Image downImage;
    private Image backDoDownImage;

    private Label[] resultLabels;

    private Label currentYutLabel;

    private Button randomYutBtn;
    private Button customYutBtn;

    private Runnable onRandomYutCallback;
    private Runnable onCustomYutCallback;

    public FxControlDisplay() {
        initialize();
    }

    private void initialize() {
        initializePane();
        createComponenets();
        layoutComponents();
    }

    private void initializePane() {
        mainPane = new VBox(20);
        mainPane.setPrefSize(350, 700);
        mainPane.setPadding(new Insets(10));
        mainPane.setAlignment(Pos.TOP_CENTER);
    }

    private void createComponenets() {
        yutPane = createYutPane();
        resultPane = createResultPane();
        currentYutPane = createCurrentYutPane();
        buttonPane = createButtonPane();
    }

    private HBox createYutPane() {
        HBox pane = new HBox(5);
        pane.setMaxSize(300, 180);
        pane.setPrefSize(300, 180);
        pane.setAlignment(Pos.CENTER);

        try {
            upImage = new Image(getClass().getResourceAsStream("/images/yut_up.png"), 60, 180, true, true);
            downImage = new Image(getClass().getResourceAsStream("/images/yut_down.png"), 60, 180, true, true);
            backDoDownImage = new Image(getClass().getResourceAsStream("/images/yut_backDo_down.png"), 60, 180, true, true);
        } catch (Exception e) {
            // 이미지 로드 실패 시 샘플 이미지 사용
            upImage = new Image("https://placehold.co/60x180", 60, 180, true, true);
            downImage = new Image("https://placehold.co/60x180",60, 180, true, true);
            backDoDownImage = new Image("https://placehold.co/60x180",60, 180, true, true);
        }

        yutSticks = new ImageView[4];
        for (int i = 0; i < 4; i++) {
            yutSticks[i] = new ImageView(upImage);
            yutSticks[i].setId("yutStick" + i);
            pane.getChildren().add(yutSticks[i]);
        }

        return pane;
    }

    private GridPane createResultPane() {
        GridPane pane = new GridPane();
        pane.setMaxSize(300, 100);
        pane.setPrefSize(300, 100);
        pane.setAlignment(Pos.CENTER);

        pane.setHgap(20);

        // 테두리 설정
        pane.setBorder(new Border(new BorderStroke(
                javafx.scene.paint.Color.GRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(5),
                BorderWidths.DEFAULT)));

        // 제목 레이블 설정
        Label titleLabel = new Label("Result");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        GridPane.setConstraints(titleLabel, 0, 0, 3, 1);
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        pane.getChildren().add(titleLabel);

        // 열 제약 조건 설정
        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.SOMETIMES);
            column.setPercentWidth(30);
            pane.getColumnConstraints().add(column);
        }

        resultLabels = new Label[3];
        for (int i = 0; i < 3; i++) {
            resultLabels[i] = new Label("-");
            resultLabels[i].setAlignment(Pos.CENTER);
            resultLabels[i].setId("resultLabel" + i);
            resultLabels[i].setFont(Font.font("System", FontWeight.BOLD, 25));
            resultLabels[i].setPadding(new Insets(5, 10, 5, 10));

            GridPane.setHalignment(resultLabels[i], HPos.CENTER);
            GridPane.setValignment(resultLabels[i], VPos.CENTER);

            resultLabels[i].setMaxWidth(Double.MAX_VALUE);

            GridPane.setConstraints(resultLabels[i], i, 1);
            pane.getChildren().add(resultLabels[i]);
        }

        return pane;
    }

    private VBox createCurrentYutPane() {
        VBox pane = new VBox();
        pane.setMaxSize(150, 80);
        pane.setPrefSize(150, 80);
        pane.setAlignment(Pos.CENTER);

        // 테두리 설정
        pane.setBorder(new Border(new BorderStroke(
                javafx.scene.paint.Color.GRAY,
                BorderStrokeStyle.SOLID,
                new CornerRadii(5),
                BorderWidths.DEFAULT)));

        // 제목 레이블 설정
        Label titleLabel = new Label("현재 윷");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        currentYutLabel = new Label("-");
        currentYutLabel.setId("currentYutLabel");
        currentYutLabel.setFont(Font.font("System", FontWeight.BOLD, 25));

        pane.getChildren().addAll(titleLabel, currentYutLabel);

        return pane;
    }

    private VBox createButtonPane() {
        VBox pane = new VBox(15);
        pane.setAlignment(Pos.CENTER);

        randomYutBtn = createButton("랜덤 윷 던지기");
        randomYutBtn.setId("randomYutBtn");
        customYutBtn = createButton("지정 윷 던지기");
        customYutBtn.setId("customYutBtn");

        randomYutBtn.setOnAction(e -> {
            if (onRandomYutCallback != null) {
                onRandomYutCallback.run();
            }
        });

        customYutBtn.setOnAction(e -> {
            if (onCustomYutCallback != null) {
                onCustomYutCallback.run();
            }
        });

        pane.getChildren().addAll(randomYutBtn, customYutBtn);

        return pane;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(150, 45);
        button.setMaxSize(150, 45);

        return button;
    }

    private void layoutComponents() {
        mainPane.getChildren().addAll(
                yutPane,
                resultPane,
                currentYutPane,
                buttonPane);
    }

    @Override
    public YutDisplay createYutDisplay() {
        return new FxYutDisplay(yutSticks, upImage, downImage, backDoDownImage);
    }

    @Override
    public ResultDisplay createResultDisplay() {
        return new FxResultDisplay(resultLabels, currentYutLabel);
    }

    @Override
    public void restorePanel() {
        mainPane.getChildren().clear();
        layoutComponents();
    }

    @Override
    public void resetCurrentYutLabel() {
        currentYutLabel.setText("-");
    }

    @Override
    public void setOnRandomYutCallback(Runnable callback) {
        this.onRandomYutCallback = callback;
    }

    @Override
    public void setOnCustomYutCallback(Runnable callback) {
        this.onCustomYutCallback = callback;
    }

    @Override
    public void enableRandomButton(boolean enabled) {
        randomYutBtn.setDisable(!enabled);
    }

    @Override
    public void enableCustomButton(boolean enabled) {
        customYutBtn.setDisable(!enabled);
    }

    @Override
    public Object getMainComponent() {
        return mainPane;
    }
}
