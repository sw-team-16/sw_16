package com.sw.yutnori.ui;

public interface GameUI {
    void initialize();
    void displayYutResult(String result);
    void updateCurrentYut(String yutType);
    void updateYutSticks(String yutType);
    void showWinner(String winnerName);
    void showError(String message);
    void closeUI();
}