package com.example.gotothemarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CourseResponse(
        boolean success,
        int status,
        Data data
) {
    public record Data(List<Course> courses) {}
    public record Course(
            int order,
            @JsonProperty("store_id") int storeId,
            @JsonProperty("store_name") String storeName,
            @JsonProperty("store_type") int storeType,
            List<String> keywords
    ) {}
}