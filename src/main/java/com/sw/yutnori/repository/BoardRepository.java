package com.sw.yutnori.repository;
import com.sw.yutnori.board.Node;
import com.sw.yutnori.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByGame_GameId(Long gameId);
}
