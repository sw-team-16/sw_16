package com.sw.yutnori.model;

import com.sw.yutnori.model.enums.BoardType;
import com.sw.yutnori.model.enums.GameState;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Game {
    private Long id;
    private BoardType boardType;
    private int numPlayers;
    private int numPieces;
    private GameState state;
    private Player currentTurnPlayer;
    private Player winnerPlayer;
    private List<Player> players = new ArrayList<>();
    private List<Turn> turns;
    private List<Board> boards;
}

