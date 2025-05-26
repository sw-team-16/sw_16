package com.sw.yutnori.ui.javafx;

import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.ui.UIFactory;
import com.sw.yutnori.ui.display.GameSetupDisplay;
import com.sw.yutnori.ui.panel.StatusPanel;
import com.sw.yutnori.ui.panel.YutBoardPanel;
import com.sw.yutnori.ui.panel.YutControlPanel;

import java.util.List;

public class FxUIFactory implements UIFactory {

    @Override
    public YutBoardPanel createYutBoardPanel(Board board) {
        return null;
    }

    @Override
    public YutControlPanel createYutControlPanel(InGameController gameController) {
        return null;
    }

    @Override
    public StatusPanel createStatusPanel(List<GameSetupDisplay.PlayerInfo> players, int pieceCount) {
        return null;
    }
}
