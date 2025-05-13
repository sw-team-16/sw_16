package com.sw.yutnori.backend.service;

import com.sw.yutnori.backend.dto.piece.response.PieceInfoResponse;


public interface PieceService {

    PieceInfoResponse findByPieceId(Long pieceId);
}