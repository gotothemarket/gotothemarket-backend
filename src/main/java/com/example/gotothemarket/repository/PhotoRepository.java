package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    // 특정 상점의 모든 사진 조회 (최신순)
    List<Photo> findByStoreStoreIdOrderByCreatedAtDesc(Integer storeId);

}