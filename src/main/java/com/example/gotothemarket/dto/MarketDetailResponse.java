package com.example.gotothemarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketDetailResponse {
    private Integer marketId;
    private String marketName;
    private String marketAddress;
    private Integer openingYears;
    private String openingCycle;
    private Integer storeCount;
    private String transport;
    private Boolean parking;
    private Boolean toilet;
    private List<String> marketMainImageUrls;
    private List<String> marketEventImageUrls;

    // marketCoord에서 추출한 좌표만
    private Double latitude;
    private Double longitude;
}
