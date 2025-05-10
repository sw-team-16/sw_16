package com.sw.yutnori.service;

import com.sw.yutnori.domain.Piece;
import com.sw.yutnori.dto.game.request.MovePieceRequest;
import com.sw.yutnori.dto.piece.response.PieceInfoResponse;
import com.sw.yutnori.repository.PieceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface PieceService {

    PieceInfoResponse findByPieceId(Long pieceId);
}