package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.*;
import jakarta.persistence.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    private int numPlayers;
    private int numPieces;

    @Enumerated(EnumType.STRING)
    private GameState state;

    @ManyToOne
    @JoinColumn(name = "current_turn_player_id")
    private Player currentTurnPlayer;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Turn> turns = new ArrayList<>();
}

