package com.sw.yutnori.repository;

import com.sw.yutnori.domain.Board;
import com.sw.yutnori.domain.PathNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PathNodeRepository extends JpaRepository<PathNode, Long> {

    List<PathNode> findByBoard(Board board);

    // 실제 좌표 (x, y) 기준 조회
    Optional<PathNode> findByBoardAndXAndY(Board board, int x, int y);

    // 논리 좌표 (a, b) 기준 조회
    Optional<PathNode> findByBoardAndAAndB(Board board, int a, int b);

}

