package com.sw.yutnori.service;

import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.YutThrowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    @Override
    public void createGame(GameCreateRequest request) {
        // 구현
    }

    @Override
    public void addPlayers(Long gameId, List<PlayerRequest> players) {
        //  구현
    }

    @Override
    public YutThrowResponse throwYutRandom(Long gameId, AutoThrowRequest request) {
        //  실제 윷 던지기 로직
        return new YutThrowResponse("MO", 1L);  // 예시 반환
    }

    @Override
    public void throwYutManual(Long gameId, ManualThrowRequest request) {
        //  수동 윷 입력 처리
    }

    @Override
    public List<Long> getMovablePieces(Long gameId) {
        //이동 가능한 말 조회
        return List.of(1L, 2L);  // 예시 반환
    }

    @Override
    public void movePiece(Long gameId, MovePieceRequest request) {
        // 말 이동 처리
    }
}
