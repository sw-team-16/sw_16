package com.sw.yutnori.repository;

import com.sw.yutnori.domain.Piece;
import com.sw.yutnori.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PieceRepository extends JpaRepository<Piece, Long> {
    List<Piece> findByPlayer_PlayerId(Long playerId);

    List<Piece> findByPlayer(Player player);

    List<Piece> findAllByAAndB(int a, int b);

    // 명확한 경로 지정: Piece → Player → Game → gameId
    @Modifying
    @Transactional
    @Query("DELETE FROM Piece p WHERE p.player.game.gameId = :gameId")
    void deleteByGameId(@Param("gameId") Long gameId);
}
