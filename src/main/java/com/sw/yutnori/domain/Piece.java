package com.sw.yutnori.domain;

import com.sw.yutnori.common.enums.PieceState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.sw.yutnori.repository.PathNodeRepository;
import com.sw.yutnori.domain.Board;
import com.sw.yutnori.domain.PathNode;

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

    @Column(name = "a", nullable = false)
    private int a;

    @Column(name = "b", nullable = false)
    private int b;


    @Column(nullable = false)
    private boolean isFinished = false;

    @Column(nullable = false)
    private boolean isGrouped = false; // 업기

    private Long groupId;

    @Column(nullable = false)
    private PieceState state = PieceState.READY;

    /**
     * (a, b): 논리 좌표 (메인 로직/DB용)
     * (x, y): 실제 좌표 (FE 렌더링용, FE에서만 사용)
     */
    public void setLogicalPosition(int a, int b) {
        this.a = a;
        this.b = b;
    }
    public int getA() {
        return a;
    }
    public int getB() {
        return b;
    }
}
