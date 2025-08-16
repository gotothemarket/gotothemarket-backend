package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Favorite;
import com.example.gotothemarket.entity.Member;
import com.example.gotothemarket.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    // 사용자와 상점의 즐겨찾기 관계 찾기
    Optional<Favorite> findByMemberAndStore(Member member, Store store);

    // 사용자와 상점의 즐겨찾기 존재 여부 확인
    boolean existsByMemberAndStore(Member member, Store store);

    // 사용자와 상점의 즐겨찾기 삭제
    void deleteByMemberAndStore(Member member, Store store);
}