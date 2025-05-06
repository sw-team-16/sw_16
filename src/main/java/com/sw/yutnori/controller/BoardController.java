package com.sw.yutnori.controller;

import com.sw.yutnori.dto.game.response.PathNodeResponse;
import com.sw.yutnori.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Board", description = "보드 및 경로 관련 API")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "보드 노드 목록 조회", description = "게임 ID에 해당하는 보드의 모든 노드 반환")
    @GetMapping("/board/{gameId}/nodes")
    public ResponseEntity<List<PathNodeResponse>> getBoardNodes(@PathVariable Long gameId) {
        return ResponseEntity.ok(boardService.getBoardNodes(gameId));
    }
}
