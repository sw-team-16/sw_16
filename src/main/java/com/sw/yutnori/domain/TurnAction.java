package com.sw.yutnori.domain;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultType result;

    @Column(nullable = false)
    private boolean isUsed = false;

    @ManyToOne
    @JoinColumn(name = "chosen_piece_id")
    private Piece chosenPiece;


    public enum ResultType {
        BACK_DO, DO, GAE, GEOL, YUT, MO
    }
}
