package com.sw.yutnori.ui.javafx;

import com.sw.yutnori.controller.InGameController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FxInGameFrame extends Stage {

    public FxInGameFrame(InGameController controller) {
        setTitle("윷놀이 게임");
        setOnCloseRequest(e -> System.exit(0));

        // 메인 레이아웃
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(root);

        Scene scene = new Scene(stackPane, 1500, 1000);
        setScene(scene);

        // DialogDisplay에 StackPane 주입 (화면 오버레이용)
        if (controller.getDialogDisplay() instanceof com.sw.yutnori.ui.javafx.display.FxDialogDisplay fxDialogDisplay) {
            fxDialogDisplay.setRootPane(stackPane);
        }

        // Scene이 설정된 후 패널 추가
        Platform.runLater(() -> {
            // 패널 가져오기
            Pane yutBoard = (Pane) controller.getYutBoardPanel().getMainComponent();
            VBox controlComponent = (VBox) controller.getControlPanel().getMainComponent();
            HBox statusComponent = (HBox) controller.getStatusPanel().getMainComponent();

            // 패널 배치
            root.setCenter(yutBoard);
            root.setRight(controlComponent);
            root.setBottom(statusComponent);

            // 레이아웃 강제 적용
            scene.getRoot().applyCss();
            scene.getRoot().layout();

            // 창 표시
            sizeToScene();
        });
    }
}
