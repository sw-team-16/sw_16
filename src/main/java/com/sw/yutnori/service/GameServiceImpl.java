package com.sw.yutnori.service;

import com.sw.yutnori.common.enums.GameState;
import com.sw.yutnori.common.enums.PieceState;
import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.domain.*;

import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;
import com.sw.yutnori.dto.game.response.YutThrowResponse;
import com.sw.yutnori.dto.piece.response.MovablePieceResponse;
import com.sw.yutnori.repository.*;

import com.sw.yutnori.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sw.yutnori.domain.Player;
import com.sw.yutnori.domain.Turn;
import com.sw.yutnori.repository.PlayerRepository;
import com.sw.yutnori.repository.TurnRepository;
import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final TurnRepository turnRepository;
    private final PieceRepository pieceRepository;
    private final TurnActionRepository turnActionRepository;

    @Override
    public Long createGame(GameCreateRequest request) {
        Game game = new Game();
        game.setBoardType(request.getBoardType());
        game.setNumPlayers(request.getPlayers().size());
        game.setNumPieces(request.getNumPieces());
        game.setState(GameState.SETUP);
        game = gameRepository.save(game);

        for (PlayerInitRequest playerReq : request.getPlayers()) {
            Player player = new Player();
            player.setName(playerReq.getName());
            player.setColor(playerReq.getColor());
            player.setGame(game);
            player.setNumOfPieces(request.getNumPieces());
            player.setFinishedCount(0);
            player = playerRepository.save(player);

            for (int i = 0; i < request.getNumPieces(); i++) {
                Piece piece = new Piece();
                piece.setPlayer(player);
                piece.setState(PieceState.READY);
                piece.setFinished(false);
                piece.setGrouped(false);
                pieceRepository.save(piece);
            }
        }

        return game.getGameId();
    }


    @Override
    @Transactional
    public YutThrowResponse throwYutRandom(Long gameId, ManualThrowRequest request) { // 수동 윷 던지기
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid playerId"));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));
        Piece piece = pieceRepository.findById(request.getPieceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid pieceId"));

        Turn turn = new Turn();
        turn.setPlayer(player);
        turn.setGame(game);
        turnRepository.save(turn);

        return new YutThrowResponse(request.getResult(), turn.getTurnId());
    }



    @Override
    public void throwYutManual(Long gameId, ManualThrowRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid playerId"));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));

        Turn turn = new Turn();
        turn.setPlayer(player);
        turn.setGame(game);
        // 지정된 결과 저장 필요 시 TurnResult로 확장 가능
        turnRepository.save(turn);
    }

    @Override
    public List<Long> getMovablePieces(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));
        Player player = game.getCurrentTurnPlayer();

        List<Piece> pieces = pieceRepository.findByPlayer_PlayerId(player.getPlayerId());
        return pieces.stream()
                .filter(p -> !p.isFinished())
                .map(Piece::getPieceId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void movePiece(MovePieceRequest request) {
        Piece movingPiece = pieceRepository.findById(request.getChosenPieceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid piece ID"));

        Player owner = movingPiece.getPlayer();

        // 목표 좌표에 있는 다른 말들 조회
        List<Piece> targetPieces = pieceRepository.findByXAndYAndState(request.getXcoord(), request.getYcoord(), PieceState.ON_BOARD);

        for (Piece target : targetPieces) {
            if (target.getPieceId().equals(movingPiece.getPieceId())) continue;

            if (target.getPlayer().getPlayerId().equals(owner.getPlayerId())) {
                // 업기
                target.setGrouped(true);
                movingPiece.setGrouped(true);
            } else {
                // 잡기
                target.setFinished(true);
                target.setGrouped(false);
                target.setState(PieceState.READY);
                target.setX(0);
                target.setY(1);
            }
            pieceRepository.save(target);
        }

        // 말 이동
        movingPiece.setX(request.getXcoord());
        movingPiece.setY(request.getYcoord());
        movingPiece.setState(PieceState.ON_BOARD);
        pieceRepository.save(movingPiece);

        // 골인 여부 확인
        if (request.getXcoord() == 0 && request.getYcoord() == 1) {
            owner.setFinishedCount(owner.getFinishedCount() + 1);
            playerRepository.save(owner);
        }

        // TurnAction 저장
        TurnAction action = new TurnAction();
        Turn turn = turnRepository.findById(request.getTurnId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid turnId"));
        action.setTurn(turn);
        action.setMoveOrder(request.getMoveOrder());
        action.setResult(null); // 필요 시 설정
        action.setUsed(true);
        action.setChosenPiece(movingPiece);
        action.setChosenPiece(movingPiece);
        turnActionRepository.save(action);
    }



    private YutResult getRandomYutResult() {
        List<YutResult> values = Arrays.asList(YutResult.values());
        List<Double> weights = List.of(0.3, 0.3, 0.2, 0.1, 0.1); // DO, GAE, GEOL, YUT, MO
        double rnd = Math.random();
        double sum = 0.0;
        for (int i = 0; i < values.size(); i++) {
            sum += weights.get(i);
            if (rnd <= sum) return values.get(i);
        }
        return values.get(values.size() - 1);
    }
    @Override
    public AutoThrowResponse getRandomYutResultForPlayer(Long gameId, AutoThrowRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid playerId"));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));

        Turn turn = new Turn();
        turn.setPlayer(player);
        turn.setGame(game);
        turn = turnRepository.save(turn);

        YutResult result = getRandomYutResult();
        return new AutoThrowResponse(result, turn.getTurnId());
    }

    @Override
    public YutThrowResponse applyRandomYutResult(Long gameId, AutoThrowApplyRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid playerId"));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));
        Piece piece = pieceRepository.findById(request.getPieceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid pieceId"));

        Turn turn = turnRepository.findById(request.getTurnId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid turnId"));

        return new YutThrowResponse(request.getResult(), turn.getTurnId());
    }
    @Override
    public List<MovablePieceResponse> getMovablePiecesByPlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid playerId"));

        List<Piece> pieces = pieceRepository.findByPlayer_PlayerId(player.getPlayerId());

        return pieces.stream()
                .filter(p -> {
                    PieceState state = p.getState();
                    return state == PieceState.READY || state == PieceState.ON_BOARD;
                })
                .map(p -> new MovablePieceResponse(p.getPieceId(), p.getState().name()))
                .collect(Collectors.toList());
    }


}
