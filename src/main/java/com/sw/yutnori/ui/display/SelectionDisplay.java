package com.sw.yutnori.ui.display;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public interface SelectionDisplay {

    List<String> getSelectedYuts();

    void updateSelectedYutsPanel();

    boolean isSpecialResult(String result);

    void setOnConfirmCallback(Consumer<List<String>> callback);
    void setOnCancelCallback(Runnable callback);

    JPanel getPanel();

}
