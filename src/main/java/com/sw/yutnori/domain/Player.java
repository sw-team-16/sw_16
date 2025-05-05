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
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    private String name;
    private String color;
    private int numOfPieces;
    private int finishedCount;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;


    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Piece> pieces = new ArrayList<>();
}
