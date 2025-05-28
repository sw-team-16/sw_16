package com.sw.yutnori.ui.javafx.panel;

import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.Player;
import com.sw.yutnori.ui.panel.YutBoardPanel;
import javafx.scene.layout.HBox;

import java.util.List;

// !TODO: JavaFX 버전으로 구현 부탁드립니다.
public class FxYutBoardPanel extends HBox implements YutBoardPanel {

    @Override
    public void renderPieceObjects(List<Piece> pieces) {

    }

    @Override
    public void setInGameController(InGameController controller) {

    }

    @Override
    public void setGameManager(GameManager gameManager) {

    }

    @Override
    public void refreshAllPieceMarkers(List<Player> players) {

    }

    // InGameFrame 클래스에서 사용
    @Override
    public Object getMainComponent() {
        return this;
    }
}
