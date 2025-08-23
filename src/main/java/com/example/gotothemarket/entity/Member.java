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

    /**
     * 실제 보유 여부 확인(배지 ID 기준)
     */
    public boolean hasBadgeId(Integer badgeId) {
        if (badgeId == null) return false;
        return userBadges.stream()
                .anyMatch(ub -> Boolean.TRUE.equals(ub.isAcquired()) && badgeId.equals(ub.getBadgeId()));
    }

}