package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.StoreVibeKey;
import com.example.gotothemarket.entity.StoreVibeStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreVibeStatRepository extends JpaRepository<StoreVibeStat, StoreVibeKey> {
    List<StoreVibeStat> findByStoreStoreIdOrderByRatioDesc(Integer storeId);
    List<StoreVibeStat> findByStoreStoreIdAndVibeVibeTypeVibeTypeId(Integer storeId, Integer vibeTypeId);
}
