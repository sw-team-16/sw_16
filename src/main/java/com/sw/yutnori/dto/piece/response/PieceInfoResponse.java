package com.sw.yutnori.dto.piece.response;

import com.sw.yutnori.common.enums.PieceState;
import com.sw.yutnori.domain.Piece;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PieceInfoResponse {

    private Long pieceId;          // 말의 ID
    private int a;                 // 논리 좌표 a
    private int b;                 // 논리 좌표 b
    private boolean isFinished;    // 완료 여부
    private boolean isGrouped;     // 업기 여부
    private Long groupId;          // 그룹 ID (업기 관련)
    private PieceState state;      // 말의 상태 (READY, MOVING 등)

    // Piece 엔티티를 PieceInfoResponse
    public static PieceInfoResponse fromEntity(Piece piece) {
        return new PieceInfoResponse(
                piece.getPieceId(),
                piece.getA(),
                piece.getB(),
                piece.isFinished(),
                piece.isGrouped(),
                piece.getGroupId(),
                piece.getState()
        );
    }
}
