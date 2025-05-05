package com.sw.yutnori.domain;

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