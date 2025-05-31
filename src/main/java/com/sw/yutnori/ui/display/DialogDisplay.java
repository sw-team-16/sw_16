package com.sw.yutnori.ui.display;

import com.sw.yutnori.model.Player;

public interface DialogDisplay {

    void showErrorDialog(String message);

    void showWinnerDialog(String winnerName);

    void showNoOnboardPieceDialog();

    void showGoalDialog(String playerName, int pieceNumber);

    void showCaptureDialog();

    void showCarryDialog(String groupedPieces);

    void showOneMoreTurnDialog();

    Object pieceSelctionDialog(Player player, String[] displayOptions);

    Object yutSelectionDialog(String[] displayOptions);
}
