package com.sw.yutnori.ui;

import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.ui.display.GameSetupDisplay;
import com.sw.yutnori.ui.panel.StatusPanel;
import com.sw.yutnori.ui.panel.YutBoardPanel;
import com.sw.yutnori.ui.panel.YutControlPanel;

import java.util.List;

public interface UIFactory {

    YutBoardPanel createYutBoardPanel(Board board);

    YutControlPanel createYutControlPanel(InGameController gameController);

    StatusPanel createStatusPanel(List<GameSetupDisplay.PlayerInfo> players, int pieceCount);
}
