package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 생성 시점에 자동으로 현재 시간 설정
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // 정적 팩토리 메서드 - 즐겨찾기 생성
    public static Favorite createFavorite(Member member, Store store) {
        return Favorite.builder()
                .member(member)
                .store(store)
                .build();
    }

    // 즐겨찾기 추가된 지 얼마나 되었는지 확인 (일 단위)
    public long getDaysSinceFavorited() {
        return java.time.temporal.ChronoUnit.DAYS.between(createdAt.toLocalDate(), LocalDateTime.now().toLocalDate());
    }

    // 최근 즐겨찾기인지 확인 (7일 이내)
    public boolean isRecentFavorite() {
        return getDaysSinceFavorited() <= 7;
    }
}