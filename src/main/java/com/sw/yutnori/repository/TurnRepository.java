package com.sw.yutnori.repository;

import com.sw.yutnori.domain.Game;
import com.sw.yutnori.domain.Turn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnRepository extends JpaRepository<Turn, Long> {
    Turn findTopByGame_GameIdOrderByTurnIdDesc(Long gameId);
    Turn findTopByGameOrderByTurnIdDesc(Game game);
}
