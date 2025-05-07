package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.YutResult;
import jakarta.persistence.*;
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
public class TurnResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "turn_id")
    private Turn turn;

    @Enumerated(EnumType.STRING)
    private YutResult result;
}
