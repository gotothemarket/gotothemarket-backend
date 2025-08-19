package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    /** 특정 상점의 모든 사진(최신순) */
    List<Photo> findByStore_StoreIdOrderByCreatedAtDesc(Integer storeId);

    /** 멤버가 업로드한 전체 사진 수 */
    long countByMember_MemberId(Integer memberId);
}