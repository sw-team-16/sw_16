package com.sw.yutnori;

public class MainApp {
    public static void main(String[] args) {
        String uiType = "Swing"; // 기본적으로 명령어를 사용하지 않는 경우 Swing 사용
        for (String arg : args) {
            if (arg.startsWith("--ui=")) {
                uiType = arg.substring("--ui=".length());
            }
        }
        if ("swing".equalsIgnoreCase(uiType)) {
            com.sw.yutnori.ui.swing.SwingGameSetupFrame.main(args);
        } else {
            com.sw.yutnori.ui.javafx.GameSetupUI.launch(args);
        }
    }
} 