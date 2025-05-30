package com.sw.yutnori.ui.javafx.display;

import com.sw.yutnori.model.Player;
import com.sw.yutnori.ui.display.DialogDisplay;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;

public class FxDialogDisplay implements DialogDisplay {

    @Override
    public void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 게임 종료 알림
    @Override
    public void showWinnerDialog(String winnerName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("게임 종료");
        alert.setHeaderText(null);
        alert.setContentText(winnerName + "님이 승리했습니다!");
        alert.showAndWait();
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
