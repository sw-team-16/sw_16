package com.sw.yutnori.controller;

import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.*;
import com.sw.yutnori.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<Void> createGame(@RequestBody GameCreateRequest request) {
        gameService.createGame(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/players")
    public ResponseEntity<Void> addPlayers(@PathVariable Long gameId,
                                           @RequestBody List<PlayerRequest> players) {
        gameService.addPlayers(gameId, players);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/turn/random")
    public ResponseEntity<YutThrowResponse> throwYutRandom(@PathVariable Long gameId,
                                                           @RequestBody AutoThrowRequest request) {
        return ResponseEntity.ok(gameService.throwYutRandom(gameId, request));
    }

    @PostMapping("/{gameId}/turn/manual")
    public ResponseEntity<Void> throwYutManual(@PathVariable Long gameId,
                                               @RequestBody ManualThrowRequest request) {
        gameService.throwYutManual(gameId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}/turn/movable-pieces")
    public ResponseEntity<List<Long>> getMovablePieces(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getMovablePieces(gameId));
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<Void> movePiece(@PathVariable Long gameId,
                                          @RequestBody MovePieceRequest request) {
        gameService.movePiece(gameId, request);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{gameId}/turn")
    public ResponseEntity<?> getTurnInfo(@PathVariable Long gameId) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameStatusResponse> getGameStatus(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGameStatus(gameId));
    }

    @GetMapping("/{gameId}/winner")
    public ResponseEntity<GameWinnerResponse> getWinner(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getWinner(gameId));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{gameId}/restart")
    public ResponseEntity<Void> restartGame(@PathVariable Long gameId, @RequestBody RestartGameRequest request) {
        gameService.restartGame(gameId, request.getWinnerPlayerId());
        return ResponseEntity.ok().build();
    }
}
