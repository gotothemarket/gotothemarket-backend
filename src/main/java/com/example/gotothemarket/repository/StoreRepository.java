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

    // 반경 내 가게 검색용 Projection
    interface NearbyStoreProjection {
        Integer getStoreId();
        String getStoreName();
        Double getLatitude();
        Double getLongitude();
        String getTypeName();
        Double getDistanceMeters();
    }

    @Query("select s.storeCoord as storeCoord, s.storeName as storeName, s.storeId as storeId, " +
            "s.storeType.storeTypeId as storeType from Store s where s.storeId = :id")
    Optional<StoreLoc> findLocById(@Param("id") int id);

    // 기본 상점 정보만 조회 (member, market, storeType)
    @Query("SELECT s FROM Store s " +
            "LEFT JOIN FETCH s.member m " +
            "LEFT JOIN FETCH s.market mk " +
            "LEFT JOIN FETCH s.storeType st " +
            "WHERE s.storeId = :storeId")
    Optional<Store> findStoreWithBasicDetailsById(@Param("storeId") Integer storeId);

    // 사진과 함께 조회 (MultipleBagFetchException 방지를 위해 photos만)
    @Query("SELECT DISTINCT s FROM Store s " +
            "LEFT JOIN FETCH s.member m " +
            "LEFT JOIN FETCH s.market mk " +
            "LEFT JOIN FETCH s.storeType st " +
            "LEFT JOIN FETCH s.photos p " +
            "WHERE s.storeId = :storeId")
    Optional<Store> findStoreWithPhotosById(@Param("storeId") Integer storeId);

    // 리뷰와 함께 조회 (MultipleBagFetchException 방지를 위해 reviews만)
    @Query("SELECT DISTINCT s FROM Store s " +
            "LEFT JOIN FETCH s.reviews r " +
            "LEFT JOIN FETCH r.member rm " +
            "WHERE s.storeId = :storeId")
    Optional<Store> findStoreWithReviewsById(@Param("storeId") Integer storeId);

    // Home API용 - 모든 가게 좌표
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

    // Home API용 - storeTypeId로 필터링
    @Query(value = "SELECT s.store_id as storeId, " +
            "ST_Y(s.store_coord) as latitude, " +
            "ST_X(s.store_coord) as longitude, " +
            "st.store_type as storeTypeId, " +
            "st.type_name as storeTypeName " +
            "FROM store s " +
            "LEFT JOIN store_type st ON s.store_type = st.store_type " +
            "WHERE s.store_coord IS NOT NULL " +
            "AND st.store_type = :storeTypeId",
            nativeQuery = true)
    List<StoreCoordProjection> findAllStoreCoordsWithStoreTypeFilter(@Param("storeTypeId") Integer storeTypeId);

    // 멤버별 가게 수 카운트
    int countByMember_MemberId(Integer memberId);

    // 반경 내 가게 검색 (Store 엔티티 반환)
    @Query(value = """
    SELECT s.* FROM store s
    WHERE ST_DWithin(
        s.store_coord::geography,
        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
        :radiusMeters
    )
    """, nativeQuery = true)
    List<Store> findStoresWithinRadius(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters
    );

    // 반경 내 가게 검색 (캐싱용 Projection 반환)
    @Query(value = """
    SELECT 
        s.store_id as storeId,
        s.store_name as storeName,
        ST_Y(s.store_coord) as latitude,
        ST_X(s.store_coord) as longitude,
        st.type_name as typeName,
        ST_Distance(
            s.store_coord::geography,
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography
        ) as distanceMeters
    FROM store s
    JOIN store_type st ON s.store_type = st.store_type
    WHERE ST_DWithin(
        s.store_coord::geography,
        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
        :radiusMeters
    )
    ORDER BY distanceMeters
    LIMIT 5
    """, nativeQuery = true)
    List<NearbyStoreProjection> findNearbyStoresForCaching(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters
    );
}