/*
 * GameServiceImpl.java
 * 백엔드 서비스 로직 구현
 * 
 * 
 */
package com.sw.yutnori.service;

import com.sw.yutnori.common.enums.GameState;
import com.sw.yutnori.common.enums.PieceState;
import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.domain.*;
import com.sw.yutnori.dto.game.response.*;
import com.sw.yutnori.dto.game.request.*;
import com.sw.yutnori.dto.game.response.AutoThrowResponse;
import com.sw.yutnori.dto.game.response.YutThrowResponse;
import com.sw.yutnori.dto.piece.response.MovablePieceResponse;
import com.sw.yutnori.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sw.yutnori.domain.Player;
import com.sw.yutnori.domain.Turn;
import com.sw.yutnori.repository.PlayerRepository;
import com.sw.yutnori.repository.TurnRepository;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
    public GameCreateResponse createGame(GameCreateRequest request) {
        Game game = new Game();
        game.setBoardType(request.getBoardType());
        game.setNumPlayers(request.getPlayers().size());
        game.setNumPieces(request.getNumPieces());
        game.setState(GameState.SETUP);
        game = gameRepository.save(game);

        List<GameCreateResponse.PlayerInfo> playerInfoList = new ArrayList<>();
        for (PlayerInitRequest playerReq : request.getPlayers()) {
            Player player = new Player();
            player.setName(playerReq.getName());
            player.setColor(playerReq.getColor());
            player.setGame(game);
            player.setNumOfPieces(request.getNumPieces());
            player.setFinishedCount(0);
            player = playerRepository.save(player);

            playerInfoList.add(new GameCreateResponse.PlayerInfo(player.getPlayerId(), player.getName(), player.getColor()));

            for (int i = 0; i < request.getNumPieces(); i++) {
                Piece piece = new Piece();
                piece.setPlayer(player);
                piece.setState(PieceState.READY);
                piece.setFinished(false);
                piece.setGrouped(false);
                pieceRepository.save(piece);
            }
        }

        return new GameCreateResponse(game.getGameId(), playerInfoList);
    }





    @Override
    public void throwYutManual(Long gameId, ManualThrowRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid playerId"));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));

        Piece piece = pieceRepository.findById(request.getPieceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid pieceId"));

        // 1. Turn 저장
        Turn turn = new Turn();
        turn.setPlayer(player);
        turn.setGame(game);
        turn = turnRepository.save(turn); // 저장 후 ID 획득

        // 2. TurnAction 저장
        TurnAction action = new TurnAction();
        action.setTurn(turn);
        action.setMoveOrder(1); // 기본값 1회차
        action.setResult(TurnAction.ResultType.valueOf(request.getResult().name()));
        action.setChosenPiece(piece);
        action.setUsed(false); // 사용 여부는 false로 초기화
        turnActionRepository.save(action);
    }



    @Override
    @Transactional
    public void movePiece(Long gameId, MovePieceRequest request) {
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
        if (request.getResult() != null) {
            action.setResult(TurnAction.ResultType.valueOf(request.getResult().name()));
        } else {
            throw new IllegalArgumentException("Yut result must not be null");
        }
        action.setUsed(true);
        action.setChosenPiece(movingPiece);
        turnActionRepository.save(action);
    }



    private YutResult getRandomYutResult() { // 랜덤한 값 반환 함수.
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




    @Override
    public GameStatusResponse getGameStatus(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        List<Piece> pieces = pieceRepository.findByPlayer_PlayerId(gameId);

        List<GameStatusResponse.PieceInfo> pieceInfos = pieces.stream().map(p ->
                new GameStatusResponse.PieceInfo(
                        p.getPieceId(),
                        p.getPlayer().getPlayerId(),
                        p.getX(),
                        p.getY(),
                        p.isFinished()
                )
        ).collect(Collectors.toList());

        return new GameStatusResponse(
                game.getGameId(),
                game.getState().name(),
                game.getBoardType().name(),
                game.getNumPlayers(),
                game.getNumPieces(),
                game.getCurrentTurnPlayer() != null ? game.getCurrentTurnPlayer().getPlayerId() : null,
                pieceInfos
        );
    }

    @Override
    public GameWinnerResponse getWinner(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (game.getState().equals(GameState.FINISHED) && game.getWinnerPlayer() != null) {
            return new GameWinnerResponse(
                    game.getWinnerPlayer().getPlayerId(),
                    game.getWinnerPlayer().getName()
            );
        } else {
            throw new IllegalStateException("Game is not finished or winner not decided yet.");
        }
    }

    @Override
    public void deleteGame(Long gameId) {
        if (!gameRepository.existsById(gameId)) {
            throw new IllegalArgumentException("Game not found: " + gameId);
        }

        gameRepository.deleteById(gameId);
    }

    @Transactional
    @Override
    public void restartGame(Long gameId, Long winnerPlayerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Player winner = playerRepository.findById(winnerPlayerId)
                .orElseThrow(() -> new IllegalArgumentException("Winner player not found"));

        game.setWinnerPlayer(winner);
        game.setState(GameState.FINISHED);
        pieceRepository.deleteByPlayerGame(gameId);

        game.setCurrentTurnPlayer(null);
        gameRepository.save(game);
    }

    @Override
    public TurnInfoResponse getTurnInfo(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("No game error"));

        Turn latestTurn = turnRepository.findTopByGame_GameIdOrderByTurnIdDesc(gameId);
        if (latestTurn == null) {
            throw new IllegalStateException("No turn error");
        }
        System.out.println(">> Turn ID: " + latestTurn.getTurnId());
        System.out.println(">> Actions size: " + latestTurn.getActions().size());
        TurnAction latestAction = latestTurn.getActions().stream()
                .max(Comparator.comparingInt(TurnAction::getMoveOrder))
                .orElseThrow(() -> new IllegalStateException("No action error"));
        for (TurnAction action : latestTurn.getActions()) {
            System.out.println("Action ID: " + action.getActionId()
                    + ", moveOrder: " + action.getMoveOrder()
                    + ", result: " + action.getResult()
                    + ", pieceId: " + (action.getChosenPiece() != null ? action.getChosenPiece().getPieceId() : "null")
                    + ", used: " + action.isUsed());
        }
        return new TurnInfoResponse(
                latestTurn.getTurnId(),
                latestTurn.getPlayer().getPlayerId(),
                latestTurn.getPlayer().getName(),
                latestAction.getResult().name(),
                latestAction.getChosenPiece() != null ? latestAction.getChosenPiece().getPieceId() : null,
                latestAction.isUsed()
        );
    }
}
