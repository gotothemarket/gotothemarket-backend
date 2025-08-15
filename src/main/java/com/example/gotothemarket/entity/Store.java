package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "store")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_type", nullable = false)
    private StoreType storeType;

    @Column(name = "store_name", length = 100, nullable = false)
    private String storeName;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @Column(name = "store_coord", columnDefinition = "geometry(Point,4326)")
    private Point storeCoord;

    @Column(name = "phone_number", length = 20, nullable = true)
    private String phoneNumber;

    @Column(name = "opening_hours", nullable = true)
    private String openingHours;

    @Column(name = "closing_hours", nullable = true)
    private String closingHours;

    @Column(name = "average_rating", precision = 2, scale = 1, nullable = true)
    private BigDecimal averageRating;

    @Column(name = "review_count", nullable = true)
    private Integer reviewCount;

    @Column(name = "store_icon", length = 255, nullable = true)
    private String storeIcon;

    @Column(name = "favorite_check", nullable = true)
    private Boolean favoriteCheck;

    // Review와의 1:N 관계 (한 상점에 여러 리뷰)
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    // Photo와의 1:N 관계 (한 상점에 여러 사진)
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Photo> photos = new ArrayList<>();

    // Favorite와의 1:N 관계 (한 상점에 여러 즐겨찾기)
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

}