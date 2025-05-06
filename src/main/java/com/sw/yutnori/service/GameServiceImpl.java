package com.sw.yutnori.service;

import com.sw.yutnori.common.enums.GameState;
import com.sw.yutnori.common.enums.PieceState;
import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.domain.*;
import com.sw.yutnori.dto.game.response.*;
import com.sw.yutnori.dto.game.request.PlayerRequest;
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
import java.util.Comparator;
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
    public void movePiece(Long gameId, MovePieceRequest request) {
        // 말 이동 로직 추후 구현
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

        // 실제 적용 로직 필요시 추가 (ex. 말 상태 업데이트 등)
        return new YutThrowResponse(request.getResult(), turn.getTurnId());
    }
    @Override
    public List<MovablePieceResponse> getMovablePiecesByPlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid playerId"));

        List<Piece> pieces = pieceRepository.findByPlayer_PlayerId(player.getPlayerId());

        return pieces.stream()
                .filter(p -> !p.isFinished()) // 도착 안 한 말만 필터
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
                        p.getXcoord(),
                        p.getYcoord(),
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

    @Transactional
    @Override
    public void addPlayersToGame(Long gameId, List<PlayerRequest> players) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        List<Player> playerEntities = players.stream().map(req -> {
            Player player = new Player();
            player.setName(req.getName());
            player.setColor(req.getColor());
            player.setNumOfPieces(req.getNumOfPieces());
            player.setFinishedCount(0);
            player.setGame(game);
            return player;
        }).toList();

        playerRepository.saveAll(playerEntities);
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
