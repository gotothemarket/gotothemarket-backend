package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.locationtech.jts.geom.Point;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    interface StoreLoc {
        Point getStoreCoord();
        String getStoreName();
        Integer getStoreId();
        Integer getStoreType(); // s.storeType.storeType
    }

    // 새로 추가할 Projection 인터페이스
    interface StoreCoordProjection {
        Integer getStoreId();
        Double getLatitude();
        Double getLongitude();
        Integer getStoreTypeId();
        String getStoreTypeName();
    }

    @Query("select s.storeCoord as storeCoord, s.storeName as storeName, s.storeId as storeId, " +
            "s.storeType.storeTypeId as storeType from Store s where s.storeId = :id")
    Optional<StoreLoc> findLocById(@Param("id") int id);

    // 기본 상점 정보만 조회
    @Query("SELECT s FROM Store s " +
            "LEFT JOIN FETCH s.member m " +
            "LEFT JOIN FETCH s.market mk " +
            "LEFT JOIN FETCH s.storeType st " +
            "WHERE s.storeId = :storeId")
    Optional<Store> findStoreWithBasicDetailsById(@Param("storeId") Integer storeId);

    // 리뷰와 함께 조회
    @Query("""
       SELECT s
       FROM Store s
       LEFT JOIN FETCH s.reviews r
       LEFT JOIN FETCH r.member rm
       WHERE s.storeId = :storeId
       """)
    Optional<Store> findStoreWithReviewsById(@Param("storeId") Integer storeId);
    // 사진과 함께 조회
    @Query("SELECT s FROM Store s " +
            "LEFT JOIN FETCH s.photos p " +
            "WHERE s.storeId = :storeId")
    Optional<Store> findStoreWithPhotosById(@Param("storeId") Integer storeId);

    // Home API용
    @Query(value = "SELECT s.store_id as storeId, " +
            "ST_Y(s.store_coord) as latitude, " +
            "ST_X(s.store_coord) as longitude, " +
            "st.store_type as storeTypeId, " +
            "st.type_name as storeTypeName " +
            "FROM store s " +
            "LEFT JOIN store_type st ON s.store_type = st.store_type " +
            "WHERE s.store_coord IS NOT NULL",
            nativeQuery = true)
    List<StoreCoordProjection> findAllStoreCoords();

    int countByMember_MemberId(Integer memberId);

}