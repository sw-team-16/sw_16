package com.sw.yutnori.service;

import com.sw.yutnori.domain.Piece;
import com.sw.yutnori.domain.Player;
import com.sw.yutnori.dto.piece.response.PieceInfoResponse;
import com.sw.yutnori.repository.PieceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PieceServiceImplTest {

    private PieceRepository pieceRepository;
    private PieceServiceImpl pieceService;

    @BeforeEach
    void setUp() {
        pieceRepository = mock(PieceRepository.class);
        pieceService = new PieceServiceImpl(pieceRepository);
    }

    @Test
    void findByPieceId_shouldReturnCorrectResponse() {
        // given
        Piece dummyPiece = new Piece();
        dummyPiece.setPieceId(1L);
        dummyPiece.setA(0);
        dummyPiece.setB(1);
        dummyPiece.setFinished(false);
        dummyPiece.setGrouped(false);
        dummyPiece.setPlayer(new Player()); // 최소한 Player가 null이 아니도록 설정
        dummyPiece.setState(com.sw.yutnori.common.enums.PieceState.READY);

        when(pieceRepository.findById(1L)).thenReturn(Optional.of(dummyPiece));

        // when
        PieceInfoResponse response = pieceService.findByPieceId(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPieceId()).isEqualTo(1L);
        assertThat(response.getA()).isEqualTo(0);
        assertThat(response.getB()).isEqualTo(1);
        assertThat(response.isFinished()).isFalse();
        assertThat(response.isGrouped()).isFalse();

        verify(pieceRepository).findById(1L);
    }
}
