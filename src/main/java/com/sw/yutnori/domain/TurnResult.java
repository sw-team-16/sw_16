package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.YutResult;
import jakarta.persistence.*;

@Entity
public class TurnResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "turn_id")
    private Turn turn;

    @Enumerated(EnumType.STRING)
    private YutResult result;
}
