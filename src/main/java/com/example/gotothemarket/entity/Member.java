package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    // Store와의 1:N 관계 (한 명의 회원이 여러 상점 소유 가능)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    // Review와의 1:N 관계 (한 명의 회원이 여러 리뷰 작성 가능)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    // Favorite와의 1:N 관계 (한 명의 회원이 여러 즐겨찾기 가능)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    // Photo와의 1:N 관계 (한 명의 회원이 여러 사진 업로드 가능)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Photo> photos = new ArrayList<>();

    // Badge와의 1:N 관계 (한 명의 회원이 여러 배지 보유 가능)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Badge> badges = new ArrayList<>();

    // 비즈니스 메서드
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    // 상점 추가
    public void addStore(Store store) {
        this.stores.add(store);
    }

    // 리뷰 추가
    public void addReview(Review review) {
        this.reviews.add(review);
    }

    // 즐겨찾기 추가
    public void addFavorite(Favorite favorite) {
        this.favorites.add(favorite);
    }

    // 사진 추가
    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    // 배지 추가
    public void addBadge(Badge badge) {
        this.badges.add(badge);
    }

    // 배지 개수 조회
    public int getBadgeCount() {
        return badges.size();
    }

    // 특정 배지 보유 여부 확인
    public boolean hasBadge(String badgeName) {
        return badges.stream()
                .anyMatch(badge -> badge.getBadgeName().equals(badgeName));
    }
}