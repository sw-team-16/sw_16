package com.sw.yutnori.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
public class GameController {

    @Operation(summary = "게임 생성", description = "새로운 게임을 생성합니다.")
    @PostMapping("/create")
    public String createGame() {
        return "게임이 생성되었습니다!";
    }
}
