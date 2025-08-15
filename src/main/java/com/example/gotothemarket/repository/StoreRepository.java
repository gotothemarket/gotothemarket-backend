package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Store;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    @Modifying
    @Transactional
    @Query(value = """

            INSERT INTO store (store_name, address, store_coord, phone_number, opening_hours, closing_hours, 
                          store_icon, favorite_check, review_count, member_id, market_id, store_type) 
        VALUES (?1, ?2, POINT(?3, ?4), ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13)
        """, nativeQuery = true)
    void saveStoreWithNativeQuery(String storeName, String address, Double longitude, Double latitude,
                                  String phoneNumber, LocalTime openingHours, LocalTime closingHours,
                                  String storeIcon, Boolean favoriteCheck, Integer reviewCount,
                                  Integer memberId, Integer marketId, Integer storeTypeId);

    @Query(value = "SELECT * FROM store ORDER BY store_id DESC LIMIT 1", nativeQuery = true)
    Store findLastInsertedStore();
    }
