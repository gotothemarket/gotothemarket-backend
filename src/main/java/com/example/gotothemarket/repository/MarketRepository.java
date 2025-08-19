package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Market;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<Market, Integer> {

    @Query(value = "SELECT * FROM market " +
            "WHERE market_coord IS NOT NULL " +
            "ORDER BY ST_Distance(market_coord, ST_SetSRID(ST_Point(:lng, :lat), 4326)) " +
            "LIMIT 1",
            nativeQuery = true)
    Market findNearestMarket(@Param("lat") double lat, @Param("lng") double lng);

    @Query(value = "SELECT market_entrance_coord FROM market WHERE market_id = :marketId", nativeQuery = true)
    Point findEntranceCoord(@Param("marketId") Integer marketId);

    @Query("select m.marketEntranceCoord from Market m where m.marketId = :id")
    Optional<Point> findEntranceCoord(@Param("id") int id);

    @Query("select m from Market m where m.marketId = :id")
    Optional<Market> findBasic(@Param("id") int id);

}