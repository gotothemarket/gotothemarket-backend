package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "store_type", nullable = true)
    private StoreType storeType;

    @Column(name = "store_name", length = 100, nullable = false)
    private String storeName;

    @Column(name = "address", length = 255, nullable = true)
    private String address;

    @Column(name = "store_coord", columnDefinition = "POINT")
    private Point storeCoord;

    @Column(name = "phone_number", length = 20, nullable = true)
    private String phoneNumber;

    @Column(name = "opening_hours", nullable = true)
    private LocalTime openingHours;

    @Column(name = "closing_hours", nullable = true)
    private LocalTime closingHours;

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

    // 비즈니스 메서드들
    public void updateRating(BigDecimal newRating) {
        this.averageRating = newRating;
    }

    public void incrementReviewCount() {
        this.reviewCount = (this.reviewCount == null) ? 1 : this.reviewCount + 1;
    }

    public void toggleFavorite() {
        this.favoriteCheck = (this.favoriteCheck == null) ? true : !this.favoriteCheck;
    }

    // 리뷰 추가
    public void addReview(Review review) {
        this.reviews.add(review);
    }

    // 사진 추가
    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    // 사진 개수 조회
    public int getPhotoCount() {
        return photos.size();
    }

    // 즐겨찾기 추가
    public void addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
    }

    // 즐겨찾기 개수 조회
    public int getFavoriteCount() {
        return favorites.size();
    }

    // 즐겨찾기 여부 확인 (특정 회원이 즐겨찾기했는지)
    public boolean isFavoritedBy(Member member) {
        return favorites.stream()
                .anyMatch(favorite -> favorite.getMember().equals(member));
    }
}