package com.sw.yutnori.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    private String name;
    private String color;
    private int numOfPieces;
    private int finishedCount;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Piece> pieces = new ArrayList<>();
}