package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.example.gotothemarket.entity.UserBadge;

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

    @Column(name = "attached_badge_id")
    private Integer attachedBadgeId;   // ← int + length 제거, null 허용

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

    // UserBadge와의 1:N (획득/장착 상태를 포함한 실제 소유 정보)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    @Builder.Default
    private List<UserBadge> userBadges = new ArrayList<>();

    // Badge와의 1:N 관계 (한 명의 회원이 여러 배지 보유 가능)
    // @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // [LEGACY] 카탈로그 엔티티(Badge) 직접 참조는 비활성화. 사용자별 소유는 UserBadge로 관리합니다.
    @Transient
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
        // 기존: 카탈로그 Badge 컬렉션 크기
        // return badges.size();
        // 변경: 실제 보유(획득)한 UserBadge만 카운트
        return (int) userBadges.stream()
                .filter(UserBadge::isAcquired)
                .count();
    }

    // 특정 배지 보유 여부 확인
    public boolean hasBadge(String badgeName) {
        // 기존(레거시): 카탈로그 Badge 이름으로 판단
        // return badges.stream().anyMatch(b -> b.getBadgeName().equals(badgeName));
        // 현재 구조에서는 UserBadge에는 badgeId만 있고 이름은 카탈로그에 있음.
        // 여기서는 레거시 컬렉션을 유지하되, 실제 보유 판단은 UserBadge 기반의 별도 메서드를 제공.
        return badges.stream().anyMatch(b -> b.getBadgeName().equals(badgeName));
    }

    /**
     * 실제 보유 여부 확인(배지 ID 기준)
     */
    public boolean hasBadgeId(Integer badgeId) {
        if (badgeId == null) return false;
        return userBadges.stream()
                .anyMatch(ub -> Boolean.TRUE.equals(ub.isAcquired()) && badgeId.equals(ub.getBadgeId()));
    }
}