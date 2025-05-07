package com.sw.yutnori.client.view;

import com.sw.yutnori.domain.Game;
import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private final PlayerInfoPanel playerInfoPanel;
    private final YutThrowPanel yutThrowPanel;
    private final LogPanel logPanel;

    public ControlPanel(GameWindow gameWindow) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 700));
        setBorder(BorderFactory.createTitledBorder("Controls"));

        playerInfoPanel = new PlayerInfoPanel();
        yutThrowPanel = new YutThrowPanel(gameWindow);
        logPanel = new LogPanel();

        add(playerInfoPanel);
        add(yutThrowPanel);
        add(logPanel);
    }

    public void updatePlayers(Game game) {
        playerInfoPanel.update(game);
    }
}
