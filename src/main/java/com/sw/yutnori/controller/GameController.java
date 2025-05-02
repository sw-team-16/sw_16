package com.sw.yutnori.controller;

import com.sw.yutnori.dto.PlayerRequest;
import com.sw.yutnori.dto.GameCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@Tag(name = "Game", description = "게임 inti 및 세팅")
public class GameController {

    @Operation(summary = "Game 생성", description = "Game을 생성하고 설정합니다.")
    @PostMapping
    public ResponseEntity<?> createGame(@RequestBody GameCreateRequest request) {
        // 로직 생략
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "player", description = "게임에 player 추가")
    @PostMapping("/{gameId}/players")
    public ResponseEntity<?> addPlayers(
            @PathVariable Long gameId,
            @RequestBody List<PlayerRequest> players) {
        // 로직 생략
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "윷 랜덤 던지기", description = "랜덤하게 윷 던져 결과 확인")
    @PostMapping("/{gameId}/turn/random")
    public ResponseEntity<?> throwYutRandom(@PathVariable Long gameId) {
        // 로직 생략
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현재 Turn Info 조회", description = "현재 Turn Info 반환")
    @GetMapping("/{gameId}/turn")
    public ResponseEntity<?> getTurnInfo(@PathVariable Long gameId) {
        // 로직 생략
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Swagger 확인용")
    @GetMapping("/test/hello")
    public String hello() {
        return "Swagger test 성공ㅇ";
    }
}
