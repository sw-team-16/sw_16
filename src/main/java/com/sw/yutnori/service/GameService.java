package com.sw.yutnori.service;

import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.YutThrowResponse;

import java.util.List;

public interface GameService {

    void createGame(GameCreateRequest request);

    void addPlayers(Long gameId, List<PlayerRequest> players);

    YutThrowResponse throwYutRandom(Long gameId, AutoThrowRequest request);

    void throwYutManual(Long gameId, ManualThrowRequest request);

    List<Long> getMovablePieces(Long gameId);

    void movePiece(Long gameId, MovePieceRequest request);
}

