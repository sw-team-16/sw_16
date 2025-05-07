package com.sw.yutnori.service;

import com.sw.yutnori.domain.Board;
import com.sw.yutnori.domain.PathNode;
import com.sw.yutnori.dto.game.response.PathNodeResponse;
import com.sw.yutnori.repository.BoardRepository;
import com.sw.yutnori.repository.PathNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PathNodeRepository pathNodeRepository;

    public List<PathNodeResponse> getBoardNodes(Long gameId) {
        Board board = boardRepository.findByGame_GameId(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        List<PathNode> nodes = pathNodeRepository.findByBoard(board);

        return nodes.stream().map(n ->
                new PathNodeResponse(
                        n.getNodeId(),
                        n.getXcoord(),
                        n.getYcoord(),
                        n.isCenter(),
                        n.isStartOrEnd(),
                        n.getNextNode() != null ? n.getNextNode().getNodeId() : null
                )
        ).collect(Collectors.toList());
    }
}
