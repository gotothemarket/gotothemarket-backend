package com.example.gotothemarket.service;

import com.example.gotothemarket.dto.KeywordTypeResponse;
import com.example.gotothemarket.entity.StoreType;
import com.example.gotothemarket.entity.Vibe;
import com.example.gotothemarket.entity.VibeType;
import com.example.gotothemarket.repository.StoreTypeRepository;
import com.example.gotothemarket.repository.VibeRepository;
import com.example.gotothemarket.repository.VibeTypeRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeywordService {

    private final StoreTypeRepository storeTypeRepository;
    private final VibeTypeRepository vibeTypeRepository;
    private final VibeRepository vibeRepository;

    public KeywordService(StoreTypeRepository storeTypeRepository,
                          VibeTypeRepository vibeTypeRepository,
                          VibeRepository vibeRepository) {
        this.storeTypeRepository = storeTypeRepository;
        this.vibeTypeRepository = vibeTypeRepository;
        this.vibeRepository = vibeRepository;
    }

    public KeywordTypeResponse getKeywordsByStoreTypeName(String storeTypeName) {
        StoreType st = storeTypeRepository.findByTypeName(storeTypeName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업종입니다: " + storeTypeName));

        // 잡화/의류 → 2,3,4  | 그 외 → 1,3,4
        boolean isGoodsOrClothes = "잡화".equals(st.getTypeName()) || "의류".equals(st.getTypeName());
        List<Integer> typeIds = isGoodsOrClothes ? List.of(2, 3, 4) : List.of(1, 3, 4);

        List<VibeType> types = vibeTypeRepository.findByVibeTypeIdInOrderByVibeTypeIdAsc(typeIds);
        List<Vibe> vibes = vibeRepository.findByVibeType_VibeTypeIdInOrderByCodeAsc(typeIds);

        Map<Integer, List<Vibe>> byType = vibes.stream()
                .collect(Collectors.groupingBy(v -> v.getVibeType().getVibeTypeId(), LinkedHashMap::new, Collectors.toList()));

        List<KeywordTypeResponse.Group> groups = new ArrayList<>();
        for (VibeType t : types) {
            List<Vibe> list = byType.getOrDefault(t.getVibeTypeId(), Collections.emptyList());
            var kws = list.stream()
                    .map(v -> new KeywordTypeResponse.Keyword(v.getCode(), v.getLabelCode(), v.getVibeName()))
                    .collect(Collectors.toList());
            groups.add(new KeywordTypeResponse.Group(t.getVibeTypeId(), t.getVibeTypeName(), kws));
        }

        return new KeywordTypeResponse(
                true, 200,
                new KeywordTypeResponse.Data(
                        new KeywordTypeResponse.StoreTypeInfo(st.getStoreType(), st.getTypeName()),
                        groups
                )
        );
    }
}