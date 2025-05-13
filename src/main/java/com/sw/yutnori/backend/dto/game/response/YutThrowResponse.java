package com.sw.yutnori.backend.dto.game.response;

import com.sw.yutnori.model.enums.YutResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YutThrowResponse {
    private String result;  // BACK_DO | DO | GAE | GEOL | YUT | MO
    private Long turnId;
    public YutThrowResponse(YutResult result, Long turnId) {
        this.result = result.name();
        this.turnId = turnId;
    }

}
