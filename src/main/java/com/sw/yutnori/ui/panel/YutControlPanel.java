package com.sw.yutnori.ui.panel;

import com.sw.yutnori.ui.display.ResultDisplay;

public interface YutControlPanel {

    void restorePanel();

    void startNewTurn();

    ResultDisplay getResultDisplay();

    void updateDisplay(String result);

    void enableRandomButton(boolean enabled);

    void enableCustomButton(boolean enabled);

    void showErrorAndRestore(String message);
}
