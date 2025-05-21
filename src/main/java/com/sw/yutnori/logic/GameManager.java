// logic/GameManager.java
// 말이도 처리(업기 및 잡기) : movePiece
/*

업힌 말과 함께 이동:movingGroup
도착지에 적이 있으면 묶음 말 전체 잡기 : targetGroup
아군 말 도착지에 있으면 업기: groupedAllyPieceIds
잡힌 말 초기화: (0,1), READY
*
* */
package com.sw.yutnori.logic;

import com.sw.yutnori.model.*;
import com.sw.yutnori.model.enums.*;
import com.sw.yutnori.ui.display.GameSetupDisplay;

import javax.swing.*;
import java.util.*;

public class GameManager {
    private Game currentGame;
    private final Map<Long, Player> playerMap = new HashMap<>();
    private final Map<Long, Piece> pieceMap = new HashMap<>();

    private final List<YutResult> yutResults = new ArrayList<>();

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
        if (r < 0.25) return YutResult.DO;
        else if (r < 0.5) return YutResult.GAE;
        else if (r < 0.7) return YutResult.GEOL;
        else if (r < 0.85) return YutResult.YUT;
        else if (r < 0.95) return YutResult.MO;
        else return YutResult.BACK_DO;  // 빽도 추가 (마지막 5% 확률)
    }


    public void addYutResult(YutResult result) {
        yutResults.add(result);
    }

    public List<YutResult> getYutResults() {
        return yutResults;
    }

    public void deleteYutResult(YutResult result) {
        yutResults.remove(result);
    }

    public void clearYutResults() {
        yutResults.clear();
    }

    // 말의 이동 로직
    public MovePieceResult movePiece(Long pieceId, YutResult result) {
        Piece piece = pieceMap.get(pieceId);
        if (piece == null) throw new IllegalArgumentException("Invalid piece ID: " + pieceId);

        // READY 상태에서 처음 보드로 이동하는 경우
        if (piece.getState() == PieceState.READY) {
            piece.setLogicalPosition(0, 1);
        }

        int a = piece.getA();
        int b = piece.getB();

        LogicalPosition dest = BoardPathManager.calculateDestination(
                pieceId, a, b, a, b, result, currentGame.getBoardType()
        );

        // 함께 이동할 아군 말들 (묶여 있는 경우 포함)
        List<Piece> movingGroup = new ArrayList<>();
        movingGroup.add(piece);
        for (Piece other : pieceMap.values()) {
            if (!other.getPieceId().equals(piece.getPieceId()) &&
                    other.getPlayer().equals(piece.getPlayer()) &&
                    other.getA() == a && other.getB() == b &&
                    other.isGrouped()) {
                movingGroup.add(other);
            }
        }

        boolean capture = false;
        boolean group = false;
        boolean finish = false;
        List<Piece> capturedPieces = new ArrayList<>();
        List<Long> groupedAllyPieceIds = new ArrayList<>();

        // 이동 위치에 적이 있으면 잡기 (묶여 있으면 전체 묶음)
        for (Piece target : pieceMap.values()) {
            if (target.getPlayer().equals(piece.getPlayer()) || target.getState() != PieceState.ON_BOARD) continue;

            if (target.getA() == dest.getA() && target.getB() == dest.getB()) {
                // 타겟 묶음까지 모두 잡기
                List<Piece> targetGroup = new ArrayList<>();
                targetGroup.add(target);
                for (Piece p : pieceMap.values()) {
                    if (!p.getPieceId().equals(target.getPieceId()) &&
                            p.getPlayer().equals(target.getPlayer()) &&
                            p.getA() == target.getA() && p.getB() == target.getB() &&
                            p.isGrouped()) {
                        targetGroup.add(p);
                    }
                }
                for (Piece p : targetGroup) {
                    p.setLogicalPosition(0, 1); // 잡힌 말은 시작 위치로
                    p.setState(PieceState.READY);
                    p.setGrouped(false);
                    capturedPieces.add(p);
                }
                capture = true;
            }
        }

        // 아군 말 업기 처리 (이동 후 같은 위치의 내 말들)
        for (Piece other : pieceMap.values()) {
            if (!other.getPieceId().equals(piece.getPieceId()) &&
                    other.getPlayer().equals(piece.getPlayer()) &&
                    other.getA() == dest.getA() && other.getB() == dest.getB()) {
                other.setGrouped(true);
                piece.setGrouped(true);
                group = true;
                groupedAllyPieceIds.add(other.getPieceId());
            }
        }

        // 이동 수행 (묶인 아군 말들 포함)
        for (Piece p : movingGroup) {
            p.setLogicalPosition(dest.getA(), dest.getB());

            // 도착점이 (0,1)이라도 실제 이동한 경우에만 FINISHED 처리
            boolean isAtGoal = (dest.getA() == 0 && dest.getB() == 1);
            boolean isMoving = !(a == dest.getA() && b == dest.getB());

            if (isAtGoal && isMoving) {
                p.setState(PieceState.FINISHED);
                p.setFinished(true);
                Player owner = p.getPlayer();
                owner.setFinishedCount(owner.getFinishedCount() + 1);
                finish = true;
            } else {
                p.setState(PieceState.ON_BOARD);
            }
        }

        return new MovePieceResult(
                capture,
                capturedPieces,
                group,
                groupedAllyPieceIds,
                finish
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
        clearYutResults();
    }


    public record MovePieceResult(
            boolean captureOccurred,
            List<Piece> capturedPieces,
            boolean groupingOccurred,
            List<Long> groupedAllyPieceIds,
            boolean reachedEndPoint
    ) {}


    // 현재 플레이어의 ON_BOARD 상태인 말이 있는지 확인
    public boolean hasOnBoardPiece(Player player) {
        for (Piece piece : player.getPieces()) {
            if (piece.getState() == PieceState.ON_BOARD) {
                return true;
            }
        }
        return false;
    }

    // 현재 플레이어의 ON_BOARD 상태 말 리스트 반환
    public List<Piece> getOnBoardPieces(Player player) {
        List<Piece> result = new ArrayList<>();
        for (Piece piece : player.getPieces()) {
            if (piece.getState() == PieceState.ON_BOARD) {
                result.add(piece);
            }
        }
        return result;
    }

    // 빽도 처리 시 말이 하나도 없으면 턴을 넘겨야 하는 조건 확인
    public boolean isBackDoTurnSkippable(Player player) {
        return !hasOnBoardPiece(player);
    }

    // 플레이어의 그룹핑된 말(같은 위치, isGrouped==true)을 그룹별로 반환
    public List<List<Piece>> getGroupedPieceLists(Player player) {
        List<Piece> onBoard = getOnBoardPieces(player);
        List<List<Piece>> groups = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        for (Piece p : onBoard) {
            if (visited.contains(p.getPieceId())) continue;
            if (p.isGrouped()) {
                // 같은 위치, 같은 player, isGrouped==true
                List<Piece> group = new ArrayList<>();
                for (Piece other : onBoard) {
                    if (other.getA() == p.getA() && other.getB() == p.getB() && other.isGrouped()) {
                        group.add(other);
                        visited.add(other.getPieceId());
                    }
                }
                // 대표 pieceId가 가장 작은 말이 그룹 대표
                group.sort(Comparator.comparing(Piece::getPieceId));
                groups.add(group);
            }
        }
        return groups;
    }

    // 그룹(말 리스트)을 "1,2,3" 식으로 표시하는 문자열 반환
    public String getGroupDisplayString(List<Piece> group) {
        if (group == null || group.isEmpty()) return "";
        Player player = group.get(0).getPlayer();
        List<Piece> all = player.getPieces();
        List<Integer> nums = new ArrayList<>();
        for (Piece p : group) {
            int idx = all.indexOf(p);
            if (idx >= 0) nums.add(idx + 1);
        }
        nums.sort(Integer::compareTo);
        return nums.stream().map(String::valueOf).reduce((a,b)->a+","+b).orElse("") ;
    }
}