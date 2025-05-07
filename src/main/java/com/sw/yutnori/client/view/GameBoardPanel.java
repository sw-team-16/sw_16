package com.sw.yutnori.client.view;

import com.sw.yutnori.domain.Game;
import javax.swing.*;
import java.awt.*;

public class GameBoardPanel extends JPanel {

    public GameBoardPanel() {
        setPreferredSize(new Dimension(700, 700));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Game Board"));
    }

    public void updateBoard(Game game) {
        // Game을 그릴 로직 필요
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(50, 50, 600, 600);
        g.drawString("Game board visualization here", 270, 350);
    }
}
