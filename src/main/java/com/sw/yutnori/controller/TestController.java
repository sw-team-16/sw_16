package com.sw.yutnori.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test API", description = "Swagger 테스트용 API")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "헬로 메세지 반환", description = "Swagger 테스트용 API입니다.")
    @GetMapping("/hello")
    public String hello() {
        return "Hello, SW Team.16 ";
    }
}
