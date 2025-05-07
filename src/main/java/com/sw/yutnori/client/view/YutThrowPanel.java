package com.sw.yutnori.client.view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class YutThrowPanel extends JPanel {
    public YutThrowPanel(GameWindow gameWindow) {
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder("Throw Yut"));

        JButton throwButton = new JButton("Throw Yut");
        throwButton.addActionListener(e -> {
            try {
                URL url = new URL("http://localhost:8080/game/throw"); // adjust endpoint
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.getInputStream().close(); // trigger the backend

                gameWindow.loadGameState();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to throw Yut: " + ex.getMessage());
            }
        });

        add(throwButton);
    }
}
