package com.example.gotothemarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CourseRequest {
    @NotNull
    @JsonProperty("market_id")
    private Integer marketId;

    @NotEmpty
    private List<Set> sets;

    public Integer getMarketId() { return marketId; }
    public void setMarketId(Integer marketId) { this.marketId = marketId; }
    public List<Set> getSets() { return sets; }
    public void setSets(List<Set> sets) { this.sets = sets; }

    public static class Set {
        @NotNull
        @JsonProperty("store_type")
        private Integer storeType;

        @NotEmpty
        private List<String> keywords;

        public Integer getStoreType() { return storeType; }
        public void setStoreType(Integer storeType) { this.storeType = storeType; }
        public List<String> getKeywords() { return keywords; }
        public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    }
}