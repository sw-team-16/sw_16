package com.sw.yutnori.controller;

import com.sw.yutnori.dto.piece.response.PieceInfoResponse;
import com.sw.yutnori.service.PieceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PieceController {

    private final PieceService pieceService;

    @Autowired
    public PieceController(PieceService pieceService) {
        this.pieceService = pieceService;
    }

    @Operation(
            summary = "S 말 클릭시 말의 정보 반환",
            description = "해당 말의 모든 정보"
    )
    @ApiResponse(responseCode = "200", description = "반환성공")
    @GetMapping("/api/pieces/{pieceId}")
    public PieceInfoResponse getPieceByPieceId(@PathVariable Long pieceId) {
        return pieceService.findByPieceId(pieceId); // 서비스 호출
    }
}
