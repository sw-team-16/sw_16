package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.PieceState;
import com.sw.yutnori.domain.value.BoardPosition;
import jakarta.persistence.*;

@Entity
public class Piece {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pieceId;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Embedded
    private BoardPosition currentPosition;

    @Enumerated(EnumType.STRING)
    private PieceState state;

    private boolean isFinished;
    private boolean isGrouped;
    private Long groupId;
}
