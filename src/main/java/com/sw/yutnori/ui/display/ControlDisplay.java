package com.sw.yutnori.ui.display;

import javax.swing.*;

public interface ControlDisplay {

    YutDisplay createYutDisplay();
    ResultDisplay createResultDisplay();

    void restorePanel();
    void resetCurrentYutLabel();

    void setOnRandomYutCallback(Runnable callback);
    void setOnCustomYutCallback(Runnable callback);

    void enableRandomButton(boolean enabled);
    void enableCustomButton(boolean enabled);

    JPanel getPanel();
}
