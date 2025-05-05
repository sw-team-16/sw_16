package com.sw.yutnori.domain;

import jakarta.persistence.*;
import java.util.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
