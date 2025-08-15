package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    // 상점 상세 정보 조회
    @Query("SELECT s FROM Store s " +
           "LEFT JOIN FETCH s.member m " +
           "LEFT JOIN FETCH s.market mk " +
           "LEFT JOIN FETCH s.storeType st " +
           "LEFT JOIN FETCH s.reviews r " +
           "LEFT JOIN FETCH r.member rm " +
           "LEFT JOIN FETCH rm.badges " +
           "LEFT JOIN FETCH s.photos p " +
           "WHERE s.storeId = :storeId")
    Optional<Store> findStoreWithDetailsById(@Param("storeId") Integer storeId);

}
