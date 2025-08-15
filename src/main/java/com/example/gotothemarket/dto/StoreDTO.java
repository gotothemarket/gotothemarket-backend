package com.example.gotothemarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// StoreDTO.java
public class StoreDTO {

    // 요청용 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreRequestDTO {
        private Integer memberId;
        private Integer marketId;
        private Integer storeTypeId;
        private String storeName;
        private String address;
        private Double latitude;
        private Double longitude;
        private String phoneNumber;

        private String openingTime;
        private String closingTime;

        private String storeIcon;
    }

    // 응답용 DTO
    @Getter
    @Builder
    public static class StoreResponseDTO {
        private Integer storeId;
        private String storeName;
        private String address;
        private Double latitude;
        private Double longitude;
        private String phoneNumber;

        private String openingTime;
        private String closingTime;

        private String storeIcon;
        private String message;
    }
}