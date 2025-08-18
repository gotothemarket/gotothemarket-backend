// HomeResponse.java
package com.example.gotothemarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeResponseDTO {
    private List<StoreCoordData> stores;
    private List<MarketCoordData> markets;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreCoordData {
        private Integer storeId;
        private Double latitude;
        private Double longitude;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MarketCoordData {
        private Integer marketId;
        private Double latitude;
        private Double longitude;
    }
}