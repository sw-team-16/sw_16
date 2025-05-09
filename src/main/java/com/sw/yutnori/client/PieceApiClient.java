package com.sw.yutnori.client;


import com.sw.yutnori.dto.piece.response.PieceInfoResponse;
import org.springframework.web.client.RestTemplate;

public class PieceApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080";

    public PieceApiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 말 ID로 말의 정보를 조회
     * @param pieceId 말 ID
     * @return PieceInfoResponse (a, b 좌표 포함)
     */
    public PieceInfoResponse getPieceInfo(Long pieceId) {
        String url = baseUrl + "/api/pieces/" + pieceId;
        return restTemplate.getForObject(url, PieceInfoResponse.class);
    }
}
