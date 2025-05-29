package com.sw.yutnori.ui.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import com.sw.yutnori.ui.javafx.FxUIFactory;
import com.sw.yutnori.controller.GameSetupController;
import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.ui.javafx.display.FxGameSetupDisplay;
import javafx.scene.Parent;

public class GameSetupUI extends Application {
    private Stage primaryStage;

    public static void launch(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("윷놀이 게임 설정");

        FxUIFactory uiFactory = new FxUIFactory();
        FxGameSetupDisplay setupDisplay = new FxGameSetupDisplay();
        Scene scene = new Scene((Parent) setupDisplay.getPanel());
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.centerOnScreen();

        GameSetupController gameSetupController = new GameSetupController(this::startGame, uiFactory);
        gameSetupController.setResultCallback(result -> {
            Alert.AlertType type = result.success() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
            Alert alert = new Alert(type);
            alert.setTitle(result.success() ? "성공" : "오류");
            alert.setHeaderText(null);
            alert.setContentText(result.message());
            alert.showAndWait();
        });
        setupDisplay.setOnStartCallback(gameSetupController::handleGameSetup);
    }

    // 게임 시작
    private void startGame(InGameController inGameController) {
        primaryStage.hide();
        FxInGameFrame gameFrame = new FxInGameFrame(inGameController);
        gameFrame.show();
    }
} 