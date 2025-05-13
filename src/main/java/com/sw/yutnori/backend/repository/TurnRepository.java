package com.sw.yutnori.backend.repository;

import com.sw.yutnori.model.Game;
import com.sw.yutnori.model.Turn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnRepository extends JpaRepository<Turn, Long> {
    Turn findTopByGame_GameIdOrderByTurnIdDesc(Long gameId);
    Turn findTopByGameOrderByTurnIdDesc(Game game);
}
