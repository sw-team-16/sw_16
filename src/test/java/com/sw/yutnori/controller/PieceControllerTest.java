package com.sw.yutnori.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.yutnori.common.enums.PieceState;
import com.sw.yutnori.dto.piece.response.PieceInfoResponse;
import com.sw.yutnori.service.PieceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PieceController.class)
@Import(PieceControllerTest.MockServiceConfig.class)
class PieceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PieceService pieceService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockServiceConfig {
        @Bean
        public PieceService pieceService() {
            return Mockito.mock(PieceService.class);
        }
    }

    @Test
    void getPieceByPieceId_shouldReturnPieceInfo() throws Exception {
        // given
        Long pieceId = 1L;
        PieceInfoResponse response = new PieceInfoResponse(
                pieceId,
                0,
                1,
                false,
                false,
                99L,
                PieceState.READY
        );

        Mockito.when(pieceService.findByPieceId(eq(pieceId))).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/pieces/{pieceId}", pieceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pieceId").value(1))
                .andExpect(jsonPath("$.a").value(0))
                .andExpect(jsonPath("$.b").value(1))
                .andExpect(jsonPath("$.finished").value(false))
                .andExpect(jsonPath("$.grouped").value(false))
                .andExpect(jsonPath("$.groupId").value(99))
                .andExpect(jsonPath("$.state").value("READY"));
    }
}
