package com.example.gotothemarket.controller;

import com.example.gotothemarket.dto.HomeResponseDTO;
import com.example.gotothemarket.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Tag(name = "홈", description = "홈 화면 API")
public class HomeController {

    private final StoreService storeService;

    @GetMapping
    @Operation(summary = "홈 화면 데이터 조회", description = "모든 상점과 마켓의 좌표 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<HomeResponseDTO> getHomeData() {
        HomeResponseDTO homeData = storeService.getHomeData();
        return ResponseEntity.ok(homeData);
    }
}