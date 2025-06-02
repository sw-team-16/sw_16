package com.sw.yutnori.ui.javafx.panel;

import com.sw.yutnori.controller.InGameController;
import com.sw.yutnori.logic.GameManager;
import com.sw.yutnori.model.Piece;
import com.sw.yutnori.model.Player;
import com.sw.yutnori.ui.panel.YutBoardPanel;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.*;
import com.sw.yutnori.model.Board;
import com.sw.yutnori.model.Node;
import com.sw.yutnori.model.enums.PieceState;
import com.sw.yutnori.ui.javafx.util.FxColorUtils;

// 윷놀이 보드 패널
public class FxYutBoardPanel extends Pane implements YutBoardPanel {
    private static final double PIECE_MARKER_SIZE = 36.0;
    private Board boardModel;
    private InGameController inGameController;
    private GameManager gameManager;
    private final Map<Long, StackPane> pieceMarkers = new HashMap<>();

    public FxYutBoardPanel(Board board) {
        this.boardModel = board;
        setPrefSize(board.getWidth(), board.getHeight());
        drawBoardBase();
    }

    private void drawBoardBase() {
        getChildren().clear();
        List<Node> nodes = boardModel.getNodes();
        for (Node node : nodes) {
            for (Node conn : node.getConnections()) {
                if (node.getA() < conn.getA() || (node.getA() == conn.getA() && node.getB() < conn.getB())) {
                    Line line = new Line(node.getX(), node.getY(), conn.getX(), conn.getY());
                    line.setStroke(Color.rgb(120, 120, 120, 0.4));
                    line.setStrokeWidth(2.0);
                    getChildren().add(line);
                }
            }
        }
        // 노드
        for (Node node : nodes) {
            double x = node.getX();
            double y = node.getY();
            boolean isStart = node.getA() == 0 && node.getB() == 1;
            boolean isCenter = node.getA() == 3 && node.getB() == 10;
            boolean isCorner = node.getA() == 5;
            Circle nodeCircle;
            // 중앙, 모퉁이 노드 = 파란색
            if (isCenter || isCorner) {
                nodeCircle = new Circle(x, y, 17);
                nodeCircle.setFill(Color.web("#789fff"));
                nodeCircle.setStroke(Color.web("#3c50a0"));
                nodeCircle.setStrokeWidth(3);
            } 
            // 시작 노드 = 노란색
            else if (isStart) {
                nodeCircle = new Circle(x, y, 17);
                nodeCircle.setFill(Color.web("#ffdc78"));
                nodeCircle.setStroke(Color.web("#c87800"));
                nodeCircle.setStrokeWidth(3);
            } else {
            // 나머지 노드 = 흰색
                nodeCircle = new Circle(x, y, 10);
                nodeCircle.setFill(Color.WHITE);
                nodeCircle.setStroke(Color.web("#6478a0"));
                nodeCircle.setStrokeWidth(2);
            }
            // 그림자 효과
            nodeCircle.setEffect(new DropShadow(4, Color.rgb(80,80,80,0.3)));
            getChildren().add(nodeCircle);
            // 시작 노드 라벨
            if (isStart) {
                Label startLabel = new Label("Start");
                startLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                startLabel.setTextFill(Color.BLACK);
                startLabel.setLayoutX(x - 12);
                startLabel.setLayoutY(y + 20);
                getChildren().add(startLabel);
            }
        }
    }

    // 말 그리기
    private StackPane createPieceMarker(String text, Color fxPlayerColor, Long pieceId, boolean isGroup) {
        StackPane marker = new StackPane();
        marker.setPrefSize(PIECE_MARKER_SIZE, PIECE_MARKER_SIZE);
        marker.setLayoutX(0);
        marker.setLayoutY(0);
        Rectangle bg = new Rectangle(PIECE_MARKER_SIZE, PIECE_MARKER_SIZE);
        bg.setArcWidth(10);
        bg.setArcHeight(10);
        bg.setFill(fxPlayerColor);
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(1);
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(fxPlayerColor.getBrightness() < 0.5 ? Color.WHITE : Color.BLACK);
        marker.getChildren().addAll(bg, label);
        marker.setUserData(pieceId);
        marker.setCursor(Cursor.HAND);
        marker.setAlignment(Pos.CENTER);
        marker.setOnMouseClicked((MouseEvent event) -> {
            if (inGameController != null) {
                inGameController.setSelectedPieceId(pieceId);
            }
        });
        return marker;
    }

    private void positionMarker(StackPane marker, Node boardNode, int offsetIndex) {
        // Offset for multiple pieces on the same node
        double offset = offsetIndex * 6.0;
        marker.setLayoutX(boardNode.getX() - PIECE_MARKER_SIZE / 2 + offset);
        marker.setLayoutY(boardNode.getY() - PIECE_MARKER_SIZE / 2 + offset);
    }

    @Override
    public void renderPieceObjects(List<Piece> pieces) {

    }

    @Override
    public void setInGameController(InGameController controller) {
        this.inGameController = controller;
    }

    @Override
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void refreshAllPieceMarkers(List<Player> players) {
        // 말 업데이트
        getChildren().removeAll(pieceMarkers.values());
        pieceMarkers.clear();
        drawBoardBase();
        Set<Long> alreadyDrawnGroupedIds = new HashSet<>();
        for (Player player : players) {
            Color fxPlayerColor = FxColorUtils.parseColor(player.getColor());
            // 그룹 말 그리기
            if (gameManager != null) {
                List<List<Piece>> grouped = gameManager.getGroupedPieceLists(player);
                for (List<Piece> group : grouped) {
                    if (group.isEmpty()) continue;
                    Piece rep = group.get(0);
                    if (rep.getState() != PieceState.ON_BOARD || rep.isFinished()) continue;
                    Node node = boardModel.findNode(rep.getA(), rep.getB());
                    if (node == null) continue;
                    String labelText = gameManager.getGroupDisplayString(group);
                    StackPane marker = createPieceMarker(labelText, fxPlayerColor, rep.getPieceId(), true);
                    positionMarker(marker, node, 0);
                    getChildren().add(marker);
                    pieceMarkers.put(rep.getPieceId(), marker);
                    for (Piece p : group) alreadyDrawnGroupedIds.add(p.getPieceId());
                }
            }
            // 개별 말 그리기
            int offsetIndex = 0;
            for (Piece piece : player.getPieces()) {
                if (piece.getState() == PieceState.ON_BOARD && !piece.isFinished() && !alreadyDrawnGroupedIds.contains(piece.getPieceId())) {
                    Node node = boardModel.findNode(piece.getA(), piece.getB());
                    if (node == null) continue;
                    int displayNum = player.getPieces().indexOf(piece) + 1;
                    StackPane marker = createPieceMarker(String.valueOf(displayNum), fxPlayerColor, piece.getPieceId(), false);
                    positionMarker(marker, node, offsetIndex++);
                    getChildren().add(marker);
                    pieceMarkers.put(piece.getPieceId(), marker);
                }
            }
        }
    }

    // InGameFrame 클래스에서 사용
    @Override
    public Object getMainComponent() {
        return this;
    }
}
