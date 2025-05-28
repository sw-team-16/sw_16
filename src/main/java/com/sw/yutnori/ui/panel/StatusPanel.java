package com.sw.yutnori.ui.panel;

import com.sw.yutnori.model.Player;

public interface StatusPanel {

    void updateCurrentPlayer(String currentPlayerName);

    void updatePlayerStatus(Player player);

    Object getMainComponent();
}
