package com.sw.yutnori.backend.dto.piece.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovablePieceResponse {
    private Long pieceId;
    private String state;
}