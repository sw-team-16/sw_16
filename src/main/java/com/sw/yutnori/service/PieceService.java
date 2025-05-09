package com.sw.yutnori.service;

import com.sw.yutnori.dto.game.request.MovePieceRequest;
import com.sw.yutnori.dto.game.response.GameStatusResponse;

public interface PieceService {
    void movePiece(Long gameId, MovePieceRequest request);
}

