package com.sw.yutnori.domain;

import jakarta.persistence.*;

@Entity
public class PathNode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nodeId;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    private int xCoord;
    private int yCoord;

    @OneToOne
    @JoinColumn(name = "next_node_id")
    private PathNode nextNode;

    private boolean isCenter;
    private boolean isStartOrEnd;
}