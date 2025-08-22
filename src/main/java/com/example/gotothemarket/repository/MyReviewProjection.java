package com.example.gotothemarket.repository;

public interface MyReviewProjection {
    Integer getStoreId();   // 추가
    String getMarketName();
    String getStoreName();
    String getContent();
}