package com.sw.yutnori.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TurnTest {

    @Test
    void constructor_setsPlayerAndGame() {
        Game game = new Game();
        Player player = new Player();
        Turn turn = new Turn();
        turn.setGame(game);
        turn.setPlayer(player);

        assertEquals(game, turn.getGame());
        assertEquals(player, turn.getPlayer());
    }

    @Test
    void setId_setsCorrectly() {
        Turn turn = new Turn();
        turn.setId(55L);
        assertEquals(55L, turn.getId());
    }
}
