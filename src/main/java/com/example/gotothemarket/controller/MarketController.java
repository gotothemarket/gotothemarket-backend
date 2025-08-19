package com.example.gotothemarket.controller;


import com.example.gotothemarket.dto.MarketDetailResponse;
import com.example.gotothemarket.entity.Market;
import com.example.gotothemarket.service.MarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
