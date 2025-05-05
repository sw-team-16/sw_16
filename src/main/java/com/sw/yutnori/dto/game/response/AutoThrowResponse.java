package com.sw.yutnori.dto.game.response;

import com.sw.yutnori.common.enums.YutResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AutoThrowResponse {
    private YutResult result;
    private Long turnId;
}
