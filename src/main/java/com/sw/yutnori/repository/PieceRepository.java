package com.sw.yutnori.repository;

import com.sw.yutnori.domain.Piece;
import com.sw.yutnori.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PieceRepository extends JpaRepository<Piece, Long> {
    List<Piece> findByPlayer_PlayerId(Long playerId);

    @Modifying
    @Query("DELETE FROM Piece p WHERE p.player.game.id = :gameId")

    void deleteByPlayerGame(@Param("gameId") Long gameId);
    List<Piece> findByPlayer(Player player);


}
