package com.example.gotothemarket.repository;

import com.example.gotothemarket.dto.TopStoreDto;

import java.util.List;

public interface RecommendRepository {
    TopStoreDto pickTopStoreByLabels(int marketId, int storeType, List<String> labelCodes);

    List<String> findMatchingKeywords(int storeId, List<String> keywords);
}