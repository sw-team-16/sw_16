package com.sw.yutnori.repository;

import com.sw.yutnori.domain.Board;
import com.sw.yutnori.domain.PathNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PathNodeRepository extends JpaRepository<PathNode, Long> {

    List<PathNode> findByBoard(Board board);

    Optional<PathNode> findByBoardAndXcoordAndYcoord(Board board, int xcoord, int ycoord);


}

