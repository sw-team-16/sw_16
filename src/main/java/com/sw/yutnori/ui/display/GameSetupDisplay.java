/*
 * GameSetupDisplay.java
 * 게임 설정 화면 구현
 * 
 * 
 * 
 */
package com.sw.yutnori.ui.display;
import javax.swing.*;

public interface GameSetupDisplay {
    record PlayerInfo(String name, String color) {}
    record SetupData(String boardType, int playerCount, int pieceCount, java.util.List<PlayerInfo> players) {}
    
    JPanel getPanel();
    void setOnStartCallback(java.util.function.Consumer<SetupData> callback);
} 