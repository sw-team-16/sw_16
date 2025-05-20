package com.sw.yutnori.model;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void defaultConstructor_initializesEmptyGame() {
        Game game = new Game();
        assertNotNull(game);
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Game game = new Game();
        Board board = new Board("square", 800, 600);
        game.setId(10L);
        game.setBoards(List.of(board));
        game.setNumPieces(4);

        assertEquals(10L, game.getId());
        assertEquals(board, game.getBoards().get(0));
        assertEquals(4, game.getNumPieces());
    }

    @Test
    void playerManagement_addAndGetPlayer() {
        Game game = new Game();
        Player player = new Player();
        player.setId(100L);
        game.getPlayers().add(player);

        assertEquals(1, game.getPlayers().size());
        assertEquals(player, game.getPlayers().get(0));
    }
}
