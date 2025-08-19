package com.example.gotothemarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MyPageFavoriteResponse {
    private boolean success;
    private int status;
    private Data data;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Data {
        private List<FavoriteDto> favorites;
        private int page;
        private int size;
        private long total;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FavoriteDto {
        private String storeName;
        private String marketName;
        private String storeIcon;
    }
}