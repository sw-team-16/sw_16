package com.sw.yutnori.service;

import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.GameStatusResponse;
import com.sw.yutnori.dto.game.response.GameWinnerResponse;
import com.sw.yutnori.dto.game.response.YutThrowResponse;
import java.util.List;

public interface GameService {

    Long createGame(GameCreateRequest request);

    void addPlayers(Long gameId, List<PlayerRequest> players);

    YutThrowResponse throwYutRandom(Long gameId, AutoThrowRequest request);

    void throwYutManual(Long gameId, ManualThrowRequest request);

    List<Long> getMovablePieces(Long gameId);

    void movePiece(Long gameId, MovePieceRequest request);

    GameStatusResponse getGameStatus(Long gameId);

    GameWinnerResponse getWinner(Long gameId);

    void deleteGame(Long gameId);

    void restartGame(Long gameId, Long winnerPlayerId);

    void addPlayersToGame(Long gameId, List<PlayerRequest> players);


}

