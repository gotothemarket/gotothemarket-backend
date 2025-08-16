package com.example.gotothemarket.service;

import com.example.gotothemarket.entity.Favorite;
import com.example.gotothemarket.entity.Member;
import com.example.gotothemarket.entity.Store;
import com.example.gotothemarket.repository.FavoriteRepository;
import com.example.gotothemarket.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public boolean toggleFavorite(Integer storeId) {
        // member_id = 1 고정
        Member member = Member.builder().memberId(1).build();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상점입니다: " + storeId));

        // 기존 즐겨찾기 관계 확인
        Optional<Favorite> existingFavorite = favoriteRepository.findByMemberAndStore(member, store);

        if (existingFavorite.isPresent()) {
            // 이미 즐겨찾기에 있으면 제거
            favoriteRepository.delete(existingFavorite.get());
            return false; // 제거됨
        } else {
            // 즐겨찾기에 없으면 추가
            Favorite newFavorite = Favorite.createFavorite(member, store);
            favoriteRepository.save(newFavorite);
            return true; // 추가됨
        }
    }
}