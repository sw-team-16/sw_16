package com.sw.yutnori.client.view;

import com.sw.yutnori.controller.*;
import com.sw.yutnori.domain.*;
import com.sw.yutnori.dto.game.response.*;
import com.sw.yutnori.dto.game.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.yutnori.service.GameService;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GameWindow extends JFrame {
    private final GameBoardPanel boardPanel;
    private final ControlPanel controlPanel;
    private Game currentGame;

    public GameWindow() {
        setTitle("Yutnori Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        boardPanel = new GameBoardPanel();
        controlPanel = new ControlPanel(this);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        loadGameState(); // Initial load
    }

    public void loadGameState() {
        try {
            URL url = new URL("http://localhost:8080/game/current"); // adjust endpoint as needed
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) json.append(scanner.nextLine());

            ObjectMapper mapper = new ObjectMapper();
            GameStatusResponse response = mapper.readValue(json.toString(), GameStatusResponse.class);
            this.currentGame = response.toGame(); // convert DTO to domain object

            boardPanel.updateBoard(currentGame);
            controlPanel.updatePlayers(currentGame);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch game state: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}
