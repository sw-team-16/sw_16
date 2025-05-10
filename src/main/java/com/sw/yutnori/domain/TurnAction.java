package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.YutResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TurnAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    @ManyToOne
    @JoinColumn(name = "turn_id", nullable = false)
    private Turn turn;

    @Column(nullable = false)
    private int moveOrder;

    @Column(nullable = false)
    private boolean isUsed = false;

    @ManyToOne
    @JoinColumn(name = "chosen_piece_id")
    private Piece chosenPiece;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private YutResult result;
}
