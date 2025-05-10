package com.sw.yutnori.controller;

import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.*;
import com.sw.yutnori.dto.piece.response.MovablePieceResponse;
import com.sw.yutnori.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @Operation(
            summary = "S1-1. 게임 생성",
            description = "게임 생성 요청을 처리하고 플레이어를 리스트로 반환"
    )
    @ApiResponse(responseCode = "200", description = "게임 생성 성공")
    @PostMapping("/create")
    public ResponseEntity<GameCreateResponse> createGame(@RequestBody GameCreateRequest request) {
        return ResponseEntity.ok(gameService.createGame(request));
    }

    @Operation(
            summary = "S2-1. 랜덤 윷 던지기",
            description = "프론트에서 선택한 윷 결과를 저장"
    )
    @ApiResponse(responseCode = "200", description = "결과 저장성공")
    @PostMapping("/{gameId}/turn/random/throw") // S 2-1 : 윷 랜덤 던지지
    public ResponseEntity<AutoThrowResponse> getRandomYutResult(@PathVariable Long gameId,
                                                                @RequestBody AutoThrowRequest request) {
        return ResponseEntity.ok(gameService.getRandomYutResultForPlayer(gameId, request));
    }
    @Operation(
            summary = "S2-3. 자동 윷 결과 값 저장",
            description = "랜덤 윷 결과를 화면에서 적용 후 DB에 저장"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @PostMapping("/{gameId}/turn/random/apply") // S 2-3 : 랜덤으로 던진 윷 적용
    public ResponseEntity<YutThrowResponse> applyRandomYutResult(@PathVariable Long gameId,
                                                                 @RequestBody AutoThrowApplyRequest request) {
        return ResponseEntity.ok(gameService.applyRandomYutResult(gameId, request));
    }


    @Operation(
            summary = "S2-2. 수동 윷 던지기",
            description = "프론트에서 윷의 결과 중 하나와 함께 적용할 말을 선택하여 db 에 저장"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @PostMapping("/{gameId}/turn/manual/throw") // S 2-3 : 윷 수동 던지기
    public ResponseEntity<Void> throwYutManual(@PathVariable Long gameId,
                                               @RequestBody ManualThrowRequest request) {
        gameService.throwYutManual(gameId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "S 3-1. 플레이어 아이디 기준 해당 플레이어의 사용 가능 말 조회",
            description = "플레이어의 아이디를 가지고 finish 라인에 들어가지 않은 말 들 반환"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @GetMapping("/player/{playerId}/movable-pieces")
    public ResponseEntity<List<MovablePieceResponse>> getMovablePieces(@PathVariable Long playerId) {
        return ResponseEntity.ok(gameService.getMovablePiecesByPlayer(playerId));
    }
    @Operation(
            summary = "S 3-2.말 이동 api",
            description = "프론트에서 좌표값 반환 시 해당 좌표에 업기 또는 잡기, finishh 여부 판단"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @PostMapping("/{gameId}/move")
    public ResponseEntity<MovePieceResponse> movePiece(
            @PathVariable Long gameId,
            @RequestBody MovePieceRequest request) {
        MovePieceResponse response = gameService.movePiece(gameId, request);
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Y 4-1. 턴 정보 조회 ",
            description = "현재 순서인 플레이어 및 턴 상태 확인"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @GetMapping("/{gameId}/turn")
    public ResponseEntity<TurnInfoResponse> getTurnInfo(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getTurnInfo(gameId));
    }

    @Operation(
            summary = "Y 4-2. 전체 게임 상태 조회",
            description = "말 위치, 종료 여부, 점수 등"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @GetMapping("/{gameId}/status")
    public ResponseEntity<GameStatusResponse> getGameStatus(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGameStatus(gameId));
    }

    @Operation(
            summary = "Y 4-3. 승리 여부 조회",
            description = "이긴 사람"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @GetMapping("/{gameId}/winner")
    public ResponseEntity<GameWinnerResponse> getWinner(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getWinner(gameId));
    }

    @Operation(
            summary = "Y 5-1. 게임 종료 ",
            description = "게임 종료, 게임 세션 삭제"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @DeleteMapping("/{gameId}/delete")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }
    @Operation(
            summary = "Y 5-2. 게임 재 시작 ",
            description = "기존 설정 유지하며 새 게임 시작"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @PostMapping("/{gameId}/restart")
    public ResponseEntity<Void> restartGame(@PathVariable Long gameId, @RequestBody RestartGameRequest request) {
        gameService.restartGame(gameId, request.getWinnerPlayerId());
        return ResponseEntity.ok().build();
    }
}
