package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.BoardType;
import com.sw.yutnori.common.enums.GameState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(nullable = false)
    private int numPlayers;

    @Column(nullable = false)
    private int numPieces;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameState state = GameState.SETUP;

    @ManyToOne
    @JoinColumn(name = "current_turn_player_id")
    private Player currentTurnPlayer;

    @ManyToOne
    @JoinColumn(name = "winner_player_id", nullable = true)
    private Player winnerPlayer;

    @OneToMany(mappedBy = "game", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Board> boards;

    @OneToMany(mappedBy = "game", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Turn> turns;


}
