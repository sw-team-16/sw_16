package com.sw.yutnori.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "path_node")
public class PathNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nodeId;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 논리 좌표 a
    @Column(nullable = false)
    private int a;

    // 논리 좌표 b
    @Column(nullable = false)
    private int b;

    // 실제 좌표 x
    @Column(nullable = false)
    private int x;

    // 실제 좌표 y
    @Column(nullable = false)
    private int y;

    @Column(nullable = false)
    private boolean isCenter = false;

    @Column(nullable = false)
    private boolean isStartOrEnd = false;
}
