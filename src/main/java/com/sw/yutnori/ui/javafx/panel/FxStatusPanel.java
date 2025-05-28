package com.sw.yutnori.ui.javafx.panel;

import com.sw.yutnori.model.Player;
import com.sw.yutnori.ui.panel.StatusPanel;
import javafx.scene.layout.HBox;

// !TODO: JavaFX 버전으로 구현 부탁드립니다.
public class FxStatusPanel extends HBox implements StatusPanel {

    @Override
    public void updateCurrentPlayer(String currentPlayerName) {

    }

    @Override
    public void updatePlayerStatus(Player player) {

    }

    // InGameFrame 클래스에서 사용
    public Object getMainComponent() {
        return this;
    }
}
