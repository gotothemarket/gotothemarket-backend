package com.example.gotothemarket.controller;


import com.example.gotothemarket.dto.MarketDetailResponse;
import com.example.gotothemarket.dto.MarketDto;
import com.example.gotothemarket.entity.Market;
import com.example.gotothemarket.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "시장", description = "시장 관련 API")
public class MarketController {
    private final MarketService marketService;

    @GetMapping("/{marketId}")
    @Operation(summary = "시장 별 조회")
    public ResponseEntity<MarketDetailResponse> getMarketDetail(@PathVariable Integer marketId) {
        MarketDetailResponse market = marketService.getMarketDetail(marketId);
        return ResponseEntity.ok(market);
    }

    @GetMapping("/nearest")
    @Operation(summary = "가장 가까운 시장 조회")
    public ResponseEntity<?> getNearestMarket(
            @RequestParam double lat,
            @RequestParam double lng) {

        MarketDto market = marketService.findNearestMarket(lat, lng);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "status", 200,
                "data", market
        ));
    }

}
