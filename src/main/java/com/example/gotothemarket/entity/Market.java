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

    @Column(name = "market_coord", columnDefinition = "geometry(Point,4326)")
    private Point marketCoord;

    @Column(name = "market_entrance_coord", columnDefinition = "geometry(Point,4326)")
    private Point marketEntranceCoord;

    @Column(name = "opening_years", nullable = true)
    private Integer openingYears;

    @Column(name = "opening_cycle", length = 50, nullable = true)
    private String openingCycle;

    @Column(name = "store_count", nullable = true)
    private Integer storeCount;

    @Column(name = "transport", length = 255, nullable = true)
    private String transport;

    @Column(name = "parking", nullable = true)
    private Boolean parking;

    @Column(name = "toilet", nullable = true)
    private Boolean toilet;

    @ElementCollection
    @CollectionTable(name = "market_main_images", joinColumns = @JoinColumn(name = "market_id"))
    @Column(name = "image_url")
    private List<String> marketMainImageUrls = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "market_event_images", joinColumns = @JoinColumn(name = "market_id"))
    @Column(name = "image_url")
    private List<String> marketEventImageUrls = new ArrayList<>();

    // Store와의 1:N 관계 (한 마켓에 여러 상점)
    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();
    
}