package com.sw.yutnori.backend.repository;
import com.sw.yutnori.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByGame_GameId(Long gameId);
}
