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
public class Piece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pieceId;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "xcoord",nullable = false)
    private Integer x;


    @Column(name = "ycoord",nullable = false)
    private Integer y;

    @Column(nullable = false)
    private boolean isFinished = false;

    @Column(nullable = false)
    private boolean isGrouped = false; // 업기


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PieceState state = PieceState.READY;

}
