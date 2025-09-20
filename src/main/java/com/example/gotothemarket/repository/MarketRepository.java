package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Market;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<Market, Integer> {
    // Projection 인터페이스
    interface MarketCoordProjection {
        Integer getMarketId();
        Double getLatitude();
        Double getLongitude();
    }
    // 점포랑 가장 가까운 시장 찾기(market_coord 사용)
    @Query(value = "SELECT * FROM market " +
            "WHERE market_coord IS NOT NULL " +
            "ORDER BY ST_Distance(market_coord, ST_SetSRID(ST_Point(:lng, :lat), 4326)) " +
            "LIMIT 1",
            nativeQuery = true)
    Market findNearestMarket(@Param("lat") double lat, @Param("lng") double lng);

    //Redis 직렬화 문제 해결
    @Query("SELECT m FROM Market m " +
            "LEFT JOIN FETCH m.marketMainImageUrls " +
            "LEFT JOIN FETCH m.marketEventImageUrls " +
            "WHERE m.marketId = :marketId")
    Optional<Market> findByIdWithImages(@Param("marketId") Integer marketId);
    
    // Home API용
    @Query(value = "SELECT market_id as marketId, " +
            "ST_Y(market_coord) as latitude, " +
            "ST_X(market_coord) as longitude " +
            "FROM market " +
            "WHERE market_coord IS NOT NULL",
            nativeQuery = true)
    List<MarketCoordProjection> findAllMarketCoords();
  
    @Query(value = "SELECT market_entrance_coord FROM market WHERE market_id = :marketId", nativeQuery = true)
    Point findEntranceCoord(@Param("marketId") Integer marketId);

    @Query("select m.marketEntranceCoord from Market m where m.marketId = :id")
    Optional<Point> findEntranceCoord(@Param("id") int id);

    @Query("select m from Market m where m.marketId = :id")
    Optional<Market> findBasic(@Param("id") int id);

}