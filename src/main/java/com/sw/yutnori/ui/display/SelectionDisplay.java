package com.sw.yutnori.ui.display;

import java.util.List;
import java.util.function.Consumer;

public interface SelectionDisplay {

    void updateSelectedYutsPanel();

    void setOnConfirmCallback(Consumer<List<String>> callback);
    void setOnCancelCallback(Runnable callback);

    Object getMainComponent();
}
