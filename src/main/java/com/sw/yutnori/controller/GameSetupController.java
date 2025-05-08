/*
 * GameSetupController.java
 * 게임 설정 화면에서 입력받은 데이터를 서버에 전송
 * 
 * 
 * 
 */
package com.sw.yutnori.controller;

import com.sw.yutnori.ui.display.GameSetupDisplay;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

public class GameSetupController {
    private static final String API_URL = "http://localhost:8080";

    // Callback: (성공 여부, 메시지)
    private Consumer<Result> resultCallback;

    public void setResultCallback(Consumer<Result> callback) {
        this.resultCallback = callback;
    }

    public void handleGameSetup(GameSetupDisplay.SetupData data) {
        // 플레이어 정보 누락 체크
        List<GameSetupDisplay.PlayerInfo> players = data.players();
        for (GameSetupDisplay.PlayerInfo p : players) {
            if (p.name() == null || p.name().isEmpty() || p.color() == null || p.color().isEmpty()) {
                if (resultCallback != null) resultCallback.accept(new Result(false, "플레이어 정보가 누락되었습니다."));
                return;
            }
        }
        // 중복 이름, 중복 색상 체크
        java.util.Set<String> names = new java.util.HashSet<>();
        java.util.Set<String> colors = new java.util.HashSet<>();
        for (GameSetupDisplay.PlayerInfo p : players) {
            if (!names.add(p.name())) {
                if (resultCallback != null) resultCallback.accept(new Result(false, "플레이어 간 이름이 중복됩니다: '" + p.name() + "'"));
                return;
            }
            if (!colors.add(p.color())) {
                if (resultCallback != null) resultCallback.accept(new Result(false, "플레이어 간 색상이 중복됩니다: '" + p.color() + "'"));
                return;
            }
        }
        String boardTypeKor = data.boardType();
        String boardType = "SQUARE";
        if ("오각형".equals(boardTypeKor)) boardType = "PENTAGON";
        else if ("육각형".equals(boardTypeKor)) boardType = "HEXAGON";

        // JSON body 생성
        StringBuilder playersJson = new StringBuilder("[");
        for (int i = 0; i < players.size(); i++) {
            var p = players.get(i);
            playersJson.append(String.format("{\"name\":\"%s\",\"color\":\"%s\"}", p.name(), p.color()));
            if (i < players.size() - 1) playersJson.append(",");
        }
        playersJson.append("]");
        String json = String.format("{\"boardType\":\"%s\",\"players\":%s,\"numPieces\":%d}",
                boardType, playersJson, data.pieceCount());

        // API 요청
        try {
            // `api/game/game/create` 요청
            URL url = new URI(API_URL + "/api/game/game/create").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }
            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                if (resultCallback != null) resultCallback.accept(new Result(true, "게임 설정이 서버에 전송되었습니다!"));
            } else {
                if (resultCallback != null) resultCallback.accept(new Result(false, "서버 오류:\n" + responseCode));
            }
        } catch (Exception ex) {
            if (resultCallback != null) resultCallback.accept(new Result(false, "API 요청 실패:\n" + ex.getMessage()));
        }
    }

    public record Result(boolean success, String message) {}
} 