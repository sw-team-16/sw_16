package com.sw.yutnori.ui.panel;

import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.Player;

import java.util.List;

public interface YutBoardPanel {

    void renderPieceObjects(List<Piece> pieces);

    void setInGameController(InGameController controller);

    void setGameManager(GameManager gameManager);

    void refreshAllPieceMarkers(List<Player> players);

    Object getMainComponent();
}
