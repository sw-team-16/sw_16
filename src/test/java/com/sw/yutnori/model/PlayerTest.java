package com.sw.yutnori.model;

import com.sw.yutnori.logic.util.ColorUtils;
import com.sw.yutnori.model.enums.PieceState;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void constructor_setsNameAndColor() {
        Player player = new Player();
        player.setName("Alice");
        player.setColor("BLUE");
        assertEquals("Alice", player.getName());
        assertEquals("BLUE", player.getColor());

        // 실제 색상 비교는 ColorUtils로 변환 후 비교
        Color expectedColor = ColorUtils.parseColor("BLUE");
        Color actualColor = ColorUtils.parseColor(player.getColor());
        assertEquals(expectedColor, actualColor);
    }

    @Test
    void addPiece_increasesPieceCount() {
        Player player = new Player();
        Piece piece = new Piece();
        piece.setPlayer(player);

        // pieces 리스트 초기화
        player.setPieces(new ArrayList<>());
        player.getPieces().add(piece);

        assertEquals(1, player.getPieces().size());
        assertEquals(piece, player.getPieces().get(0));
    }

    @Test
    void finishedCount_returnsCorrectCount() {
        Player player = new Player();

        Piece p1 = new Piece(); p1.setFinished(true);  p1.setState(PieceState.FINISHED); p1.setPlayer(player);
        Piece p2 = new Piece(); p2.setFinished(false); p2.setPlayer(player);

        player.setPieces(new ArrayList<>());
        player.getPieces().add(p1);
        player.getPieces().add(p2);

        assertEquals(1, player.getFinishedCount());
    }

}
