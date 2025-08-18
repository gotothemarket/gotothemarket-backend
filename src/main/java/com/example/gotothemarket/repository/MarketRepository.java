package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketRepository extends JpaRepository<Market, Integer> {

    @Query(value = "SELECT * FROM market " +
            "WHERE market_coord IS NOT NULL " +
            "ORDER BY ST_Distance(market_coord, ST_SetSRID(ST_Point(:lng, :lat), 4326)) " +
            "LIMIT 1",
            nativeQuery = true)
    Market findNearestMarket(@Param("lat") double lat, @Param("lng") double lng);
}