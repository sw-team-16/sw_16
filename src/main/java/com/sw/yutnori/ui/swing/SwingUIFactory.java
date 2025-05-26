package com.sw.yutnori.ui.swing;

import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.ui.UIFactory;
import com.sw.yutnori.ui.display.GameSetupDisplay;
import com.sw.yutnori.ui.panel.StatusPanel;
import com.sw.yutnori.ui.panel.YutBoardPanel;
import com.sw.yutnori.ui.panel.YutControlPanel;
import com.sw.yutnori.ui.swing.panel.SwingStatusPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutBoardPanel;
import com.sw.yutnori.ui.swing.panel.SwingYutControlPanel;

import java.util.List;

public class SwingUIFactory implements UIFactory {

    @Override
    public YutBoardPanel createYutBoardPanel(Board board) {
        return new SwingYutBoardPanel(board);
    }

    @Override
    public YutControlPanel createYutControlPanel(InGameController gameController) {
        return new SwingYutControlPanel(gameController);
    }

    @Override
    public StatusPanel createStatusPanel(List<GameSetupDisplay.PlayerInfo> players, int pieceCount) {
        return new SwingStatusPanel(players, pieceCount);
    }
}
