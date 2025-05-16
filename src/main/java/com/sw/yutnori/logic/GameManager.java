// logic/GameManager.java
package com.sw.yutnori.logic;

import com.sw.yutnori.model.*;
import com.sw.yutnori.model.enums.*;
import com.sw.yutnori.ui.display.GameSetupDisplay;

import java.util.*;

public class GameManager {
    private Game currentGame;
    private final Map<Long, Player> playerMap = new HashMap<>();
    private final Map<Long, Piece> pieceMap = new HashMap<>();

    private long pieceIdCounter = 1;

    private long generatePieceId() {
        return pieceIdCounter++;
    }
    public void createGameFromSetupData(GameSetupDisplay.SetupData data) {
        Game game = new Game();
        game.setId(System.currentTimeMillis()); // 임시 ID 생성
        game.setBoardType(parseBoardType(data.boardType()));
        game.setNumPlayers(data.players().size());
        game.setNumPieces(data.pieceCount());
        game.setState(GameState.PLAYING);
        game.setTurns(new ArrayList<>());
        game.setPlayers(new ArrayList<>());

        createPlayersFromSetup(game, data);
        game.setCurrentTurnPlayer(game.getPlayers().get(0)); // 첫 번째 플레이어를 현재 턴으로 설정

        this.currentGame = game;

        // 모든 말 정보를 pieceMap에 저장
        for (Player p : game.getPlayers()) {
            for (Piece piece : p.getPieces()) {
                pieceMap.put(piece.getPieceId(), piece);
            }
        }

        // 첫 턴 생성
        Turn initialTurn = new Turn();
        initialTurn.setId(1L);
        initialTurn.setGame(game);
        initialTurn.setPlayer(game.getCurrentTurnPlayer());
        game.getTurns().add(initialTurn);
    }
    private BoardType parseBoardType(String boardTypeKor) {
        return switch (boardTypeKor) {
            case "오각형" -> BoardType.PENTAGON;
            case "육각형" -> BoardType.HEXAGON;
            default -> BoardType.SQUARE;
        };
    }





    private List<Piece> createInitialPieces(Player player, int pieceCount) {
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < pieceCount; i++) {
            Piece piece = new Piece();
            piece.setPieceId(generatePieceId()); // ID 부여 메서드 필요
            piece.setPlayer(player);
            piece.setA(0);  // 시작 위치 A
            piece.setB(1);  // 시작 위치 B
            piece.setFinished(false);
            piece.setGrouped(false);
            piece.setState(PieceState.READY);
            pieces.add(piece);
        }
        return pieces;
    }

    private void createPlayersFromSetup(Game game, GameSetupDisplay.SetupData data) {
        long idCounter = 1;
        for (GameSetupDisplay.PlayerInfo info : data.players()) {
            Player player = new Player();
            player.setId(idCounter++);
            player.setName(info.name());
            player.setColor(info.color());
            player.setGame(game);
            player.setFinishedCount(0);
            player.setNumOfPieces(data.pieceCount());
            player.setPieces(createInitialPieces(player, data.pieceCount()));
            game.getPlayers().add(player);
            playerMap.put(player.getId(), player); // 반드시 필요
        }
    }


    public Game getCurrentGame() {
        return currentGame;
    }

    public YutResult generateRandomYut() {
        double r = Math.random();
        if (r < 0.3) return YutResult.DO;
        else if (r < 0.6) return YutResult.GAE;
        else if (r < 0.8) return YutResult.GEOL;
        else if (r < 0.95) return YutResult.YUT;
        else return YutResult.MO;
    }

    public MovePieceResult movePiece(Long pieceId, YutResult result) {
        Piece piece = pieceMap.get(pieceId);
        if (piece == null) throw new IllegalArgumentException("Invalid piece ID: " + pieceId);

        // READY 상태에서 처음 보드로 이동하는 경우, 시작점에서 출발
        if (piece.getState() == PieceState.READY) {
            piece.setLogicalPosition(0, 1);
        }

        int a = piece.getA();
        int b = piece.getB();

        LogicalPosition dest = BoardPathManager.calculateDestination(
                pieceId,
                a,
                b,
                a,
                b,
                result,
                currentGame.getBoardType()
        );

        // 잡기 또는 업기 판단
        boolean capture = false;
        boolean group = false;
        List<Piece> capturedPieces = new ArrayList<>();
        List<Long> groupedAllyPieceIds = new ArrayList<>();
        for (Piece other : pieceMap.values()) {
            if (!other.getPlayer().equals(piece.getPlayer()) &&
                    other.getA() == dest.getA() && other.getB() == dest.getB()) {
                // 잡힌 말 처리: 상태를 READY로, 위치를 (0,0)으로, isGrouped를 false로
                other.setLogicalPosition(0, 0); // 대기 위치로 이동
                other.setState(PieceState.READY);
                other.setGrouped(false);
                capture = true;
                capturedPieces.add(other);
            } else if (other.getPlayer().equals(piece.getPlayer()) &&
                    other.getA() == dest.getA() && other.getB() == dest.getB() &&
                    !other.getPieceId().equals(pieceId)) {
                other.setGrouped(true);
                piece.setGrouped(true);
                group = true;
                groupedAllyPieceIds.add(other.getPieceId());
            }
        }

        // 말 이동 처리
        piece.setLogicalPosition(dest.getA(), dest.getB());

        // 골인 지점 도달 시 처리
        boolean finish = dest.getA() == 0 && dest.getB() == 1;
        if (finish) {
            piece.setState(PieceState.FINISHED);
            piece.setFinished(true);
            Player owner = piece.getPlayer();
            owner.setFinishedCount(owner.getFinishedCount() + 1);
        } else {
            piece.setState(PieceState.ON_BOARD);
        }

        boolean moreTurn = result == YutResult.YUT || result == YutResult.MO || capture;

        return new MovePieceResult(
            capture,
            capturedPieces,
            group,
            groupedAllyPieceIds,
            finish,
            moreTurn
        );
    }

    public Turn getCurrentTurn() {
        List<Turn> turns = currentGame.getTurns();
        return turns.isEmpty() ? null : turns.get(turns.size() - 1);
    }

    public Player getPlayer(Long playerId) {
        return playerMap.get(playerId);
    }

    public Piece getPiece(Long pieceId) {
        return pieceMap.get(pieceId);
    }

    public void nextTurn(Long currentPlayerId) {
        List<Player> players = currentGame.getPlayers();
        players.sort(Comparator.comparing(Player::getId)); // 반드시 정렬 필요

        int index = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId().equals(currentPlayerId)) {
                index = i;
                break;
            }
        }
        int nextIndex = (index + 1) % players.size();
        currentGame.setCurrentTurnPlayer(players.get(nextIndex)); // 이 줄 꼭 필요
    }


    public record MovePieceResult(
            boolean captureOccurred,
            List<Piece> capturedPieces,
            boolean groupingOccurred,
            List<Long> groupedAllyPieceIds,
            boolean reachedEndPoint,
            boolean requiresAnotherMove
    ) {}
}