package com.sw.yutnori.service;

import com.sw.yutnori.common.enums.GameState;
import com.sw.yutnori.common.enums.PieceState;
import com.sw.yutnori.common.enums.YutResult;
import com.sw.yutnori.domain.*;
import com.sw.yutnori.dto.game.response.*;
import com.sw.yutnori.dto.game.request.PlayerRequest;
import com.sw.yutnori.dto.game.request.*;
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
        game.setNumPlayers(request.getNumPlayers());
        game.setNumPieces(request.getNumPieces());
        game.setState(GameState.SETUP);
        return gameRepository.save(game).getGameId();
    }

    @Override
    public void addPlayers(Long gameId, List<PlayerRequest> requestList) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));

        for (PlayerRequest request : requestList) {
            Player player = new Player();
            player.setName(request.getName());
            player.setColor(request.getColor());
            player.setNumOfPieces(game.getNumPieces());
            player.setFinishedCount(0);
            playerRepository.save(player);

            for (int i = 0; i < game.getNumPieces(); i++) {
                Piece piece = new Piece();
                piece.setPlayer(player);
                piece.setState(PieceState.START);
                piece.setFinished(false);
                piece.setGrouped(false);
                pieceRepository.save(piece);
            }
        }
    }

    @Override
    @Transactional
    public YutThrowResponse throwYutRandom(Long gameId, AutoThrowRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid playerId"));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid gameId"));

        // 랜덤 윷 결과 선택
        YutResult result = getRandomYutResult();

        Turn turn = new Turn();
        turn.setPlayer(player);
        turn.setGame(game);
        turnRepository.save(turn);

        return new YutThrowResponse(result, turn.getTurnId());
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
                .orElseThrow(() -> new IllegalArgumentException("해당 게임이 존재하지 않습니다."));

        Turn latestTurn = turnRepository.findTopByGame_GameIdOrderByTurnIdDesc(gameId);
        if (latestTurn == null) {
            throw new IllegalStateException("해당 게임의 턴 정보가 없습니다.");
        }

        TurnAction latestAction = latestTurn.getActions().stream()
                .max(Comparator.comparingInt(TurnAction::getMoveOrder))
                .orElseThrow(() -> new IllegalStateException("해당 턴의 액션 정보가 없습니다."));

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
