/*
 * GameServiceImpl.java
 * 프론트엔드에서 게임 관련 API 요청을 처리하는 서비스 클래스
 * 
 * 
 * 
 */
package com.sw.yutnori.service;

import com.sw.yutnori.board.Node;
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
    private final BoardRepository boardRepository;

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

    // 노드 기반 로직 구현을 위한 함수
    private Node handleJunction(Node currentNode) {
        // 단순 구현: 첫 번째 연결 노드를 선택
        return currentNode.getConnections().get(0);
    }

    // 골인 노드 확인 함수
    private boolean isGoalNode(Node node) {
        // 논리 좌표 a=0, b=0이면 골인지점으로 간주
        return node.getA() == 0 && node.getB() == 0;
    }


    @Override
    @Transactional
    public void movePiece(Long gameId, MovePieceRequest request) {
        boolean capturedOpponentPiece = false;

        // 이동할 말 id로 가져오기
        Piece movingPiece = pieceRepository.findById(request.getChosenPieceId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid piece ID"));

        Player owner = movingPiece.getPlayer();

        // 현재 위치의 Node 저장
        Node currentNode = boardRepository.findNodeByCoordinates(movingPiece.getA(), movingPiece.getB());

        // 이동할 노드 찾기
        Node targetNode = boardRepository.findNodeByCoordinates(request.getAcoord(), request.getBcoord());

        // 분기점 처리: 연결된 노드가 여러 개인 경우
        if (currentNode.getConnections().size() > 1) {
            targetNode = handleJunction(currentNode); // 분기점 처리 추가 로직
        }
        // 중간점 처리: 연결된 노드가 하나인 경우
        else if (currentNode.getConnections().size() == 1) {
            targetNode = currentNode.getConnections().get(0); // 다음 노드로 이동
        }

        // 목표 위치에서 다른 피스 충돌 처리
        List<Piece> targetPieces = pieceRepository.findByAAndBAndState(
                targetNode.getA(), targetNode.getB(), PieceState.ON_BOARD);

        for (Piece target : targetPieces) {
            if (target.getPieceId().equals(movingPiece.getPieceId())) continue;

            if (target.getPlayer().getPlayerId().equals(owner.getPlayerId())) {
                // 같은 팀: 업기 처리
                target.setGrouped(true);
                movingPiece.setGrouped(true);
            } else {
                // 다른 팀: 잡기 처리
                target.setFinished(true);
                target.setGrouped(false);
                target.setState(PieceState.READY); // 보드에서 다시 시작
                target.setA(0);
                target.setB(0); // 시작점으로 리셋
                capturedOpponentPiece = true;
            }

            // 저장
            pieceRepository.save(target);
        }

        // 피스 이동 적용
        movingPiece.setA(targetNode.getA());
        movingPiece.setB(targetNode.getB());
        movingPiece.setState(PieceState.ON_BOARD);
        pieceRepository.save(movingPiece);

        // 골인지점 처리
        if (isGoalNode(targetNode)) {
            owner.setFinishedCount(owner.getFinishedCount() + 1); // 골인한 피스 수 증가
            playerRepository.save(owner);
        }

        // TurnAction 기록
        TurnAction action = new TurnAction();
        Turn turn = turnRepository.findById(request.getTurnId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid turnId"));

        action.setTurn(turn);
        action.setMoveOrder(request.getMoveOrder());
        action.setUsed(true);
        action.setChosenPiece(movingPiece);
        action.setResult(TurnAction.ResultType.valueOf(request.getResult().name()));

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
