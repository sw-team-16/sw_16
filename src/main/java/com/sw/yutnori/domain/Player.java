package com.sw.yutnori.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @Column(nullable = false, length = 50)
    private String name;

    private String color;

    @Column(nullable = false)
    private int numOfPieces;

    @Column(nullable = false)
    private int finishedCount = 0;
    @ManyToOne

    @JoinColumn(name = "game_id")
    private Game game;


}
