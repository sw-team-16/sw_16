package com.sw.yutnori.ui.javafx.display;

import com.sw.yutnori.model.Player;
import com.sw.yutnori.ui.display.DialogDisplay;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class FxDialogDisplay implements DialogDisplay {

    private StackPane rootPane;

    public void setRootPane(StackPane rootPane) {
        this.rootPane = rootPane;
    }

    @Override
    public void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Runnable onRestartCallback;

    @Override
    public void showWinnerDialog(String winnerName) {
        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.rgb(0, 0, 0, 0.7));
        overlay.widthProperty().bind(rootPane.widthProperty());
        overlay.heightProperty().bind(rootPane.heightProperty());
        overlay.setMouseTransparent(false);
        
        rootPane.getChildren().add(overlay);
        
        javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("게임 종료");
        dialog.setHeaderText(null);
        dialog.setContentText(winnerName + "님이 승리했습니다!");
        
        javafx.scene.control.ButtonType restartBtn = new javafx.scene.control.ButtonType("재시작", javafx.scene.control.ButtonBar.ButtonData.YES);
        javafx.scene.control.ButtonType exitBtn = new javafx.scene.control.ButtonType("종료", javafx.scene.control.ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().setAll(restartBtn, exitBtn);
        dialog.setResizable(false);
        dialog.setGraphic(null);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.DECORATED);

        dialog.setOnShown(e -> {
            javafx.stage.Window dialogWindow = dialog.getDialogPane().getScene().getWindow();
            javafx.stage.Window parentWindow = rootPane.getScene().getWindow();

            javafx.application.Platform.runLater(() -> {
                double parentCenterX = parentWindow.getX() + parentWindow.getWidth() / 2;
                double parentCenterY = parentWindow.getY() + parentWindow.getHeight() / 2;

                dialogWindow.setX(parentCenterX - dialogWindow.getWidth() / 2);
                dialogWindow.setY(parentCenterY - dialogWindow.getHeight() / 2);
            });
        });
        
        java.util.Optional<javafx.scene.control.ButtonType> result = dialog.showAndWait();
        
        rootPane.getChildren().remove(overlay);
        
        if (result.isPresent()) {
            if (result.get() == restartBtn) {
                if (onRestartCallback != null) onRestartCallback.run();
            } else if (result.get() == exitBtn) {
                javafx.application.Platform.exit();
            }
        }
    }

    // 게임 재시작 콜백을 주입 (MainApp/GameSetupUI 등에서 호출 필요)
    public void setOnRestartCallback(Runnable callback) {
        this.onRestartCallback = callback;
    }
    
    @Override
    public void showNoOnboardPieceDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("빽도");
        alert.setHeaderText(null);
        alert.setContentText("OnBoard 상태의 말이 없어 턴을 넘깁니다.");
        alert.showAndWait();
    }

    @Override
    public void showGoalDialog(String playerName, int pieceNumber) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("완주");
        alert.setHeaderText(null);
        alert.setContentText(playerName + "님의 " + pieceNumber + "번 말이 도착지에 도달했습니다!");
        alert.showAndWait();
    }

    @Override
    public void showCaptureDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("잡기");
        alert.setHeaderText(null);
        alert.setContentText("상대 말을 잡았습니다!");
        alert.showAndWait();
    }

    @Override
    public void showCarryDialog(String groupedPieces) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("업기");
        alert.setHeaderText(null);
        alert.setContentText("같은 위치의 아군 말을 업었습니다: " + groupedPieces);
        alert.showAndWait();
    }

    @Override
    public void showOneMoreTurnDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("추가 턴");
        alert.setHeaderText(null);
        alert.setContentText("한 번 더 이동할 수 있습니다. 윷을 던지세요.");
        alert.showAndWait();
    }

    @Override
    public Object pieceSelctionDialog(Player player, String[] displayOptions) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(displayOptions[0], displayOptions);
        dialog.setTitle("말 선택");
        dialog.setHeaderText(null);
        dialog.setContentText("[" + player.getName() + "] 사용할 말을 선택하세요");

        // Optional<String>을 반환하므로 이를 처리해야 함
        return dialog.showAndWait().orElse(null);
    }

    @Override
    public Object yutSelectionDialog(String[] displayOptions) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(displayOptions[0], displayOptions);
        dialog.setTitle("윷 결과 선택");
        dialog.setHeaderText(null);
        dialog.setContentText("사용할 윷 결과를 선택하세요");

        return dialog.showAndWait().orElse(null);
    }
}