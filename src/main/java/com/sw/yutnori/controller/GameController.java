package com.sw.yutnori.controller;

import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.*;
import com.sw.yutnori.dto.piece.response.MovablePieceResponse;
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

    @PostMapping("/{gameId}/turn/random")
    public ResponseEntity<AutoThrowResponse> getRandomYutResult(@PathVariable Long gameId,
                                                                @RequestBody AutoThrowRequest request) {
        return ResponseEntity.ok(gameService.getRandomYutResultForPlayer(gameId, request));
    }
    @PostMapping("/{gameId}/turn/random/apply")
    public ResponseEntity<YutThrowResponse> applyRandomYutResult(@PathVariable Long gameId,
                                                                 @RequestBody AutoThrowApplyRequest request) {
        return ResponseEntity.ok(gameService.applyRandomYutResult(gameId, request));
    }



    @PostMapping("/{gameId}/turn/manual")
    public ResponseEntity<Void> throwYutManual(@PathVariable Long gameId,
                                               @RequestBody ManualThrowRequest request) {
        gameService.throwYutManual(gameId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/player/{playerId}/movable-pieces")
    public ResponseEntity<List<MovablePieceResponse>> getMovablePieces(@PathVariable Long playerId) {
        return ResponseEntity.ok(gameService.getMovablePiecesByPlayer(playerId));
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<Void> movePiece(@PathVariable Long gameId,
                                          @RequestBody MovePieceRequest request) {
        gameService.movePiece(gameId, request);    // 빌드 에러 수정
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{gameId}/turn")
    public ResponseEntity<TurnInfoResponse> getTurnInfo(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getTurnInfo(gameId));
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
