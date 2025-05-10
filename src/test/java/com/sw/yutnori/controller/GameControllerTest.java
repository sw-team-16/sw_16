package com.sw.yutnori.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.common.enums.BoardType;
import com.sw.yutnori.common.LogicalPosition;
import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.*;
import com.sw.yutnori.dto.piece.response.MovablePieceResponse;
import com.sw.yutnori.service.GameService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public GameService gameService() {
            return Mockito.mock(GameService.class);
        }
    }

    @Test
    void createGame_shouldReturnSuccess() throws Exception {
        GameCreateRequest request = new GameCreateRequest();
        PlayerInitRequest player = new PlayerInitRequest();
        player.setName("Alice");
        player.setColor("RED");
        request.setBoardType(BoardType.SQUARE);
        request.setPlayers(List.of(player));
        request.setNumPieces(2);

        GameCreateResponse.PlayerInfo info = new GameCreateResponse.PlayerInfo(1L, "Alice", "RED", List.of(1001L, 1002L));
        GameCreateResponse response = new GameCreateResponse(1L, List.of(info));
        Mockito.when(gameService.createGame(any())).thenReturn(response);

        mockMvc.perform(post("/api/game/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getRandomYutResult_shouldReturnResult() throws Exception {
        AutoThrowRequest request = new AutoThrowRequest();
        request.setPlayerId(1L);
        AutoThrowResponse response = new AutoThrowResponse(1L, YutResult.GAE);
        Mockito.when(gameService.getRandomYutResultForPlayer(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/game/1/turn/random/throw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void applyRandomYutResult_shouldReturnSuccess() throws Exception {
        AutoThrowApplyRequest request = new AutoThrowApplyRequest();
        request.setTurnId(1L);
        request.setPlayerId(1L);
        request.setPieceId(1L);
        request.setResult(YutResult.YUT);

        YutThrowResponse response = new YutThrowResponse("YUT 적용 완료");
        Mockito.when(gameService.applyRandomYutResult(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/game/1/turn/random/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void throwYutManual_shouldReturnOk() throws Exception {
        ManualThrowRequest request = new ManualThrowRequest();
        request.setPlayerId(1L);
        request.setPieceId(1L);
        request.setResult(YutResult.DO);

        mockMvc.perform(post("/api/game/1/turn/manual/throw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getMovablePieces_shouldReturnList() throws Exception {
        Mockito.when(gameService.getMovablePiecesByPlayer(any()))
                .thenReturn(List.of(new MovablePieceResponse(1L, "P1", "(0,0)")));

        mockMvc.perform(get("/api/game/player/1/movable-pieces"))
                .andExpect(status().isOk());
    }

    @Test
    void movePiece_shouldReturnOk() throws Exception {
        MovePieceRequest request = new MovePieceRequest();
        request.setPlayerId(1L);
        request.setChosenPieceId(1L);
        request.setMoveOrder(1);
        request.setA(2);
        request.setB(3);
        request.setResult(YutResult.GEOL);

        mockMvc.perform(post("/api/game/1/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getTurnInfo_shouldReturnInfo() throws Exception {
        Mockito.when(gameService.getTurnInfo(any()))
                .thenReturn(new TurnInfoResponse(1L, "Alice", true));

        mockMvc.perform(get("/api/game/1/turn"))
                .andExpect(status().isOk());
    }

    @Test
    void getGameStatus_shouldReturnStatus() throws Exception {
        GameStatusResponse.PieceInfo pieceInfo = new GameStatusResponse.PieceInfo(
                1L, 1L, 100, 150, false, 2, 3
        );

        GameStatusResponse response = new GameStatusResponse(
                1L, "IN_PROGRESS", "SQUARE", 2, 4, 1L, List.of(pieceInfo)
        );

        Mockito.when(gameService.getGameStatus(any())).thenReturn(response);

        mockMvc.perform(get("/api/game/1/status"))
                .andExpect(status().isOk());
    }

    @Test
    void getWinner_shouldReturnWinner() throws Exception {
        Mockito.when(gameService.getWinner(any()))
                .thenReturn(new GameWinnerResponse(1L, "Alice"));

        mockMvc.perform(get("/api/game/1/winner"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteGame_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/game/1/delete"))
                .andExpect(status().isNoContent());
    }

    @Test
    void restartGame_shouldReturnOk() throws Exception {
        RestartGameRequest request = new RestartGameRequest(1L);

        mockMvc.perform(post("/api/game/1/restart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
