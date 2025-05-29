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

    @Override
    public YutBoardPanel createYutBoardPanel(Board board) {
        return new FxYutBoardPanel();   // 테스트 위해 임시 제작. 구현 후 알맞은 생성자로 변경 부탁드립니다.
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
        return new FxDialogDisplay();
    }
}
