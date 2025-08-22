package com.example.gotothemarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class StoreDTO {

    // 요청용 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreRequestDTO {
        private Integer memberId;
        private Integer storeType;
        private String storeName;
        private String address;
        private StoreCoord storeCoord;
        private String phoneNumber;
        private String openingHours;
        private String closingHours;
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

    // wrapper DTO
    @Getter
    @Builder
    public static class StoreDetailResponse {
        private StoreInfo store;
        private List<PhotoInfo> photos;
        private ReviewSummary reviewSummary;
        private List<ReviewInfo> reviews;
    }

    // 상점 기본 정보 GET용
    @Getter
    @Builder
    public static class StoreInfo {
        private Integer storeId;
        private Integer memberId;
        private Integer marketId;
        private Integer storeType;
        private String typeName;
        private String storeName;
        private String address;
        private StoreCoord storeCoord;
        private String phoneNumber;
        private String openingHours;
        private String closingHours;
        private String storeIcon;
        private Boolean favoriteCheck;
    }

    // 좌표 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreCoord {
        private Double lat;
        private Double lng;
    }

    // 사진 정보
    @Getter
    @Builder
    public static class PhotoInfo {
        private Integer photoId;
        private String photoUrl;
        private LocalDateTime createdAt;
    }

    //사진 등록
    @Getter
    @Builder
    public static class PhotoUploadResponse {
        private Integer photoId;
        private String photoUrl;
        private String message;
        private LocalDateTime uploadedAt;
    }

    // 리뷰 별점, 갯수
    @Getter
    @Builder
    public static class ReviewSummary {
        private BigDecimal averageRating;
        private Integer reviewCount;
    }

    // 리뷰 정보
    @Getter
    @Builder
    public static class ReviewInfo {
        private Integer reviewId;
        private Integer memberId;
        private String memberNickname;
        private BadgeInfo badge;
        private BigDecimal rating;
        private String content;
        private LocalDateTime createdAt;
    }

    // 배지 정보
    @Getter
    @Builder
    public static class BadgeInfo {
        private Integer badgeId;
        private String badgeName;
        private String badgeIcon;
    }

    // PATCH 요청용 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreUpdateDTO {
        private String storeName;
        private String address;
        private StoreCoord storeCoord;
        private String phoneNumber;
        private String openingHours;
        private String closingHours;
        private String storeIcon;
        private Integer storeType;
    }
}