package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.BoardType;
import com.sw.yutnori.common.enums.GameState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "game")
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
    @JoinColumn(name = "winner_player_id")
    private Player winnerPlayer;


}
