package com.sw.yutnori.service;

import com.sw.yutnori.domain.Piece;
import com.sw.yutnori.dto.piece.response.PieceInfoResponse;
import com.sw.yutnori.repository.PieceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PieceServiceImpl implements PieceService {

    private final PieceRepository pieceRepository;

    @Autowired
    public PieceServiceImpl(PieceRepository pieceRepository) {
        this.pieceRepository = pieceRepository;
    }

    @Override
    public PieceInfoResponse findByPieceId(Long pieceId) {
        Piece piece = pieceRepository.findById(pieceId)
                .orElseThrow(() -> new IllegalArgumentException("말을 찾을 수 없습니다."));
        return PieceInfoResponse.fromEntity(piece);  // Piece를 DTO로 변환하여 반환
    }
}
