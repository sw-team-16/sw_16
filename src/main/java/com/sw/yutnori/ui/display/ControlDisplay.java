package com.sw.yutnori.ui.display;

public interface ControlDisplay {

    YutDisplay createYutDisplay();
    ResultDisplay createResultDisplay();

    void restorePanel();
    void resetCurrentYutLabel();

    void setOnRandomYutCallback(Runnable callback);
    void setOnCustomYutCallback(Runnable callback);

    void enableRandomButton(boolean enabled);
    void enableCustomButton(boolean enabled);

    Object getMainComponent();
}