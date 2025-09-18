package com.example.gotothemarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageFavoriteResponse {
    private boolean success;
    private int status;
    private Data data;

    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Data {
        private List<FavoriteDto> favorites;
        private int page;
        private int size;
        private long total;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FavoriteDto {
        private Integer storeId;
        private String storeName;
        private String marketName;
        private String storeIcon;
    }
}