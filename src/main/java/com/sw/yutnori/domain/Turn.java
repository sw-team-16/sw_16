package com.sw.yutnori.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Turn {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long turnId;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "chosen_piece_id")
    private Piece chosenPiece;

    @OneToMany(mappedBy = "turn", cascade = CascadeType.ALL)
    private List<TurnResult> results = new ArrayList<>();
}
