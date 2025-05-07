package com.sw.yutnori.client.view;

import com.sw.yutnori.domain.Game;
import com.sw.yutnori.domain.Player;

import javax.swing.*;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {
    private final JTextArea infoArea;

    public PlayerInfoPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Players"));
        infoArea = new JTextArea(5, 20);
        infoArea.setEditable(false);
        add(new JScrollPane(infoArea), BorderLayout.CENTER);
    }

    public void update(Game game) {
        StringBuilder sb = new StringBuilder();
        for (Player p : game.getPlayers()) {
            sb.append(p.getName()).append(" - ").append(p.getTeam()).append("\n");
        }
        infoArea.setText(sb.toString());
    }
}
