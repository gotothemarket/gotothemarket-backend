package com.example.gotothemarket.controller;


import com.example.gotothemarket.dto.MarketDetailResponse;
import com.example.gotothemarket.dto.MarketDto;
import com.example.gotothemarket.entity.Market;
import com.example.gotothemarket.service.MarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
@Slf4j
public class MarketController {
    private final MarketService marketService;

    @GetMapping("/{marketId}")
    public ResponseEntity<MarketDetailResponse> getMarketDetail(@PathVariable Integer marketId) {
        MarketDetailResponse market = marketService.getMarketDetail(marketId);
        return ResponseEntity.ok(market);
    }

    @GetMapping("/nearest")
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
