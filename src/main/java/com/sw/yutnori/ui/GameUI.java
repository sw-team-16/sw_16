package com.sw.yutnori.ui;

import com.sw.yutnori.common.enums.GameState;

public interface GameUI {
    void initialize();
    void displayYutResult(String result);
    void updateCurrentYut(String yutType);
    void updateYutSticks(String yutType);
    void showWinner(String winnerName);
    void showError(String message);
    void closeUI();
}