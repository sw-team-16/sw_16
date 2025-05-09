package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.PieceState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "piece")
public class Piece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pieceId;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "xcoord", nullable = false)
    private int x;

    @Column(name = "ycoord",nullable = false)
    private int y;

    // Logical Coordinate를 위해 추가
    @Column(name = "a", nullable = false)
    private int a;

    @Column(name = "b", nullable = false)
    private int b;

    @Column(nullable = false)
    private boolean isFinished = false;

    @Column(nullable = false)
    private boolean isGrouped = false; // 업기

    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PieceState state = PieceState.READY;

}
