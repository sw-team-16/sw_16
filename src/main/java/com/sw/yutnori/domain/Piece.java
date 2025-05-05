package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.PieceState;
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
public class Piece {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pieceId;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;


    @Enumerated(EnumType.STRING)
    private PieceState state;

    private boolean isFinished;
    private boolean isGrouped;
    private Long groupId;
}
