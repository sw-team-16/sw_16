package com.sw.yutnori.dto.game.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YutThrowResponse {
    private String result;  // BACK_DO | DO | GAE | GEOL | YUT | MO
    private Long turnId;
}
