package com.example.gotothemarket.repository;

import com.example.gotothemarket.dto.TopStoreDto;

import java.util.List;

public interface RecommendRepository {
    TopStoreDto pickTopStoreByLabels(int marketId, int storeType, List<String> labelCodes);
    /**
     * Return only label_codes among the given labels that this store has with positive frequency
     * (hit_count > 0 OR ratio > 0).
     */
    List<String> findPositiveLabelCodes(Integer storeId, List<String> labelCodes);
    List<String> findMatchingKeywords(int storeId, List<String> keywords);
}