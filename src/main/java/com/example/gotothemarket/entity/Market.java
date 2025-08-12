package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "market")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_id")
    private Integer marketId;

    @Column(name = "market_name", length = 100, nullable = false)
    private String marketName;

    @Column(name = "market_address", length = 255, nullable = true)
    private String marketAddress;

    @Column(name = "market_coord", columnDefinition = "POINT")
    private Point marketCoord;

    @Column(name = "opening_years", nullable = true)
    private Integer openingYears;

    @Column(name = "opening_cycle", length = 50, nullable = true)
    private String openingCycle;

    @Column(name = "food_store_count", nullable = true)
    private Integer foodStoreCount;

    @Column(name = "stall_store_count", nullable = true)
    private Integer stallStoreCount;

    @Column(name = "business_type", length = 100, nullable = true)
    private String businessType;

    @Column(name = "main_items", length = 255, nullable = true)
    private String mainItems;

    @Column(name = "market_number", length = 255, nullable = true)
    private String marketNumber;

    @Column(name = "transport", length = 255, nullable = true)
    private String transport;

    @Column(name = "parking", nullable = true)
    private Boolean parking;

    @Column(name = "toilet", nullable = true)
    private Boolean toilet;

    @Column(name = "tourism", length = 255, nullable = true)
    private String tourism;

    // Store와의 1:N 관계 (한 마켓에 여러 상점)
    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    // 비즈니스 메서드
    public void updateMarketInfo(String name, String address) {
        this.marketName = name;
        this.marketAddress = address;
    }

    public void updateStoreCounts(Integer foodCount, Integer stallCount) {
        this.foodStoreCount = foodCount;
        this.stallStoreCount = stallCount;
    }

    public void updateFacilities(Boolean parking, Boolean toilet) {
        this.parking = parking;
        this.toilet = toilet;
    }

    // 상점 추가
    public void addStore(Store store) {
        this.stores.add(store);
    }

    // 총 상점 수 계산
    public Integer getTotalStoreCount() {
        Integer food = (foodStoreCount != null) ? foodStoreCount : 0;
        Integer stall = (stallStoreCount != null) ? stallStoreCount : 0;
        return food + stall;
    }
}