package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.locationtech.jts.geom.Point;

import java.util.Optional;

// MarketRepository.java
public interface MarketRepository extends JpaRepository<Market, Integer> {
    @Query("select m.marketEntranceCoord from Market m where m.marketId = :id")
    Optional<Point> findEntranceCoord(@Param("id") int id);

    @Query("select m from Market m where m.marketId = :id")
    Optional<Market> findBasic(@Param("id") int id);
}