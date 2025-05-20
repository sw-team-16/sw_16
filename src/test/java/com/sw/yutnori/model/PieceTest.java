package com.sw.yutnori.model;

import com.sw.yutnori.model.enums.PieceState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    @Test
    void defaultConstructor_setsStateToREADY() {
        Player player = new Player();
        Piece piece = new Piece();
        piece.setPlayer(player);

        assertEquals(PieceState.READY, piece.getState());
        assertEquals(player, piece.getPlayer());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Player player = new Player();
        Piece piece = new Piece();
        piece.setPlayer(player);
        piece.setPieceId(42L);
        piece.setA(3);
        piece.setB(4);
        piece.setFinished(true);

        assertEquals(42L, piece.getPieceId());
        assertEquals(3, piece.getA());
        assertEquals(4, piece.getB());
        assertTrue(piece.isFinished());
    }
}
