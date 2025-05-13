package com.sw.yutnori.backend.repository;

import com.sw.yutnori.model.Game;
import com.sw.yutnori.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByGame_GameId(Long gameId); //  gameId 매핑
    List<Player> findByGame(Game game);
}
