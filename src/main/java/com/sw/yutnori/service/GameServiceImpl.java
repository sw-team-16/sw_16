/*
 * GameServiceImpl.java
 * 백엔드 서비스 로직 구현
 * 
 * 
 */
package com.sw.yutnori.service;

import com.sw.yutnori.common.enums.BoardType;
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
    private final PathNodeRepository pathNodeRepository;

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

            List<Long> pieceIds = new ArrayList<>();
            for (int i = 0; i < request.getNumPieces(); i++) {
                Piece piece = new Piece();
                piece.setPlayer(player);
                piece.setState(PieceState.READY);
                piece.setFinished(false);
                piece.setGrouped(false);
                piece = pieceRepository.save(piece);
                pieceIds.add(piece.getPieceId());
            }

            playerInfoList.add(new GameCreateResponse.PlayerInfo(
                    player.getPlayerId(),
                    player.getName(),
                    player.getColor(),
                    pieceIds
            ));
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
        action.setResult(request.getResult());
        action.setChosenPiece(piece);
        action.setUsed(false); // 사용 여부는 false로 초기화
        turnActionRepository.save(action);
    }

    @Transactional
    @Override
    public void movePiece(Long gameId, MovePieceRequest request) {
        // 1. 엔티티 조회
        Piece movingPiece = pieceRepository.findById(request.getChosenPieceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid piece ID"));
        Player owner = movingPiece.getPlayer();
        Game game = owner.getGame();
        Board board = game.getBoards().get(0); // 첫 번째 보드 사용

        // 2. 목표 노드 조회
        PathNode targetNode = pathNodeRepository.findByBoardAndAAndB(board, request.getA(), request.getB())
                .orElseThrow(() -> new IllegalArgumentException("Invalid logical coordinates (a, b)"));

        // 3. 잡기 및 업기 처리
        handleCaptureOrStacking(movingPiece, request.getA(), request.getB());

        // 4. 말 이동 및 상태 갱신
        movingPiece.setLogicalPosition(request.getA(), request.getB());
        movingPiece.setState(PieceState.ON_BOARD);
        pieceRepository.save(movingPiece);

        // 5. 골인 처리 (Game의 BoardType 기준)
        BoardType boardType = game.getBoardType();
        if (isEndPoint(request.getA(), request.getB(), boardType)) {
            owner.setFinishedCount(owner.getFinishedCount() + 1);
            playerRepository.save(owner);
        }

        // 6. TurnAction 저장
        Turn turn = turnRepository.findById(request.getTurnId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid turnId"));

        saveTurnAction(turn, request.getMoveOrder(), request.getResult(), movingPiece);
    }

    private void handleCaptureOrStacking(Piece movingPiece, int a, int b) {
        List<Piece> piecesAtTarget = pieceRepository.findAllByAAndB(a, b);
        for (Piece target : piecesAtTarget) {
            if (target.getPieceId().equals(movingPiece.getPieceId())) continue;

            if (target.getPlayer().getPlayerId().equals(movingPiece.getPlayer().getPlayerId())) {
                // 업기
                target.setGrouped(true);
                movingPiece.setGrouped(true);
            } else {
                // 잡기
                target.setFinished(true);
                target.setGrouped(false);
                target.setState(PieceState.READY);
                target.setLogicalPosition(0, 1); // 시작점 복귀
            }
            pieceRepository.save(target);
        }
    }

    private boolean isEndPoint(int a, int b, BoardType type) {
        // 모든 판 유형의 도착 지점은 (0,1)
        return a == 0 && b == 1; // 사각형 기준
    }
    private void saveTurnAction(Turn turn, int moveOrder, YutResult result, Piece piece) {
        if (result == null) throw new IllegalArgumentException("Yut result must not be null");

        TurnAction action = new TurnAction();
        action.setTurn(turn);
        action.setMoveOrder(moveOrder);
        action.setResult(result);
        action.setUsed(true);
        action.setChosenPiece(piece);
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

        List<GameStatusResponse.PieceInfo> pieceInfos = pieces.stream().map(p -> {
            int a = p.getA();
            int b = p.getB();
            int x = -1;
            int y = -1;
            // (a, b) 기준으로 PathNode를 찾아 실제 좌표 변환
            PathNode node = pathNodeRepository.findByBoardAndAAndB(p.getPlayer().getGame().getBoards().get(0), a, b).orElse(null);
            if (node != null) {
                x = node.getX();
                y = node.getY();
            }
            return new GameStatusResponse.PieceInfo(
                    p.getPieceId(),
                    p.getPlayer().getPlayerId(),
                    x,
                    y,
                    p.isFinished(),
                    a,
                    b
            );
        }).collect(Collectors.toList());

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
        pieceRepository.deleteByGameId(gameId);

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
