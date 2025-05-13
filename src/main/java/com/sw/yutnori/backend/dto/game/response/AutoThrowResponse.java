package com.sw.yutnori.backend.dto.game.response;

import com.sw.yutnori.model.enums.YutResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AutoThrowResponse {
    private YutResult result;
    private Long turnId;
}
