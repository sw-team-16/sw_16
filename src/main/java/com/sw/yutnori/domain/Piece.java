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

    @Column(nullable = false)
    private int xcoord;

    @Column(nullable = false)
    private int ycoord;

    @Column(nullable = false)
    private boolean isFinished = false;

    @Column(nullable = false)
    private boolean isGrouped = false;

    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PieceState state = PieceState.READY;

}
