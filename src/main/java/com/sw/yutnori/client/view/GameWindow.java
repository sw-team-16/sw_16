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
    private final Board board = new Board();
    private final Game game = new Game();

    public GameWindow() {
        // 기본적인 UI 설정
        setTitle("Yutnori");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


    }

}