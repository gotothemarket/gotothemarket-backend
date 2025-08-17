package com.example.gotothemarket.dto;

public class TopStoreDto {
    private final int storeId;
    private final String storeName;
    private final int storeType;
    private final double score;

    public TopStoreDto(int storeId, String storeName, int storeType, double score) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeType = storeType;
        this.score = score;
    }
    public int getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
    public int getStoreType() { return storeType; }
    public double getScore() { return score; }
}