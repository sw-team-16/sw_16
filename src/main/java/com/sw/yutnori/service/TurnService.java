package com.sw.yutnori.service;

import com.sw.yutnori.dto.game.request.AutoThrowRequest;
import com.sw.yutnori.dto.game.request.ManualThrowRequest;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;

import java.util.List;

public interface TurnService {
    AutoThrowResponse throwYutRandom(Long gameId, AutoThrowRequest request);
    void throwYutManual(Long gameId, ManualThrowRequest request);
    List<Long> getMovablePieces(Long gameId);
}

