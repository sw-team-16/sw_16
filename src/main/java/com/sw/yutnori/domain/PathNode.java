package com.sw.yutnori.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PathNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nodeId;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;


    @ManyToOne
    @JoinColumn(name = "next_node_id")
    private PathNode nextNode;

    @Column(nullable = false)
    private boolean isCenter = false;

    @Column(nullable = false)
    private boolean isStartOrEnd = false;
}
