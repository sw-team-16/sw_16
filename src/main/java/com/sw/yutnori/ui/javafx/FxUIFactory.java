package com.sw.yutnori.ui.javafx;

import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.ui.UIFactory;
import com.sw.yutnori.ui.display.DialogDisplay;
import com.sw.yutnori.ui.display.GameSetupDisplay;
import com.sw.yutnori.ui.javafx.display.FxDialogDisplay;
import com.sw.yutnori.ui.javafx.panel.FxStatusPanel;
import com.sw.yutnori.ui.javafx.panel.FxYutBoardPanel;
import com.sw.yutnori.ui.javafx.panel.FxYutControlPanel;
import com.sw.yutnori.ui.panel.StatusPanel;
import com.sw.yutnori.ui.panel.YutBoardPanel;
import com.sw.yutnori.ui.panel.YutControlPanel;

import java.util.List;

public class FxUIFactory implements UIFactory {

    private Runnable restartCallback;

    public void setRestartCallback(Runnable callback) {
        this.restartCallback = callback;
    }

    @Override
    public YutBoardPanel createYutBoardPanel(Board board) {
        return new FxYutBoardPanel(board);
    }

    @Override
    public YutControlPanel createYutControlPanel(InGameController gameController) {
        return new FxYutControlPanel(gameController);
    }

    @Override
    public StatusPanel createStatusPanel(List<GameSetupDisplay.PlayerInfo> players, int pieceCount) {
        return new FxStatusPanel(players, pieceCount);
    }

    @Override
    public DialogDisplay createDialogDisplay() {
        FxDialogDisplay dialog = new FxDialogDisplay();
        if (restartCallback != null) dialog.setOnRestartCallback(restartCallback);
        return dialog;
    }
}
