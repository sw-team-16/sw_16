package com.sw.yutnori.service;

import com.sw.yutnori.dto.game.request.MovePieceRequest;

public interface PieceService {
    void movePiece(Long gameId, MovePieceRequest request);
}
