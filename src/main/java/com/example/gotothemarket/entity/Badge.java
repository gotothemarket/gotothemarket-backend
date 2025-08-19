package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Badge 카탈로그 엔티티 (회원과 무관한 공용 정의)
 *
 * 회원별 보유/장착 상태는 UserBadge에서 관리합니다.
 */
@Entity
@Table(name = "badge")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    /**
     * 카탈로그용 고정 ID (마이그레이션/시드 SQL로 주입)
     * IDENTITY를 제거하여 애플리케이션에서 임의 생성하지 않도록 함.
     */
    @Id
    @Column(name = "badge_id")
    private Integer badgeId;

    @Column(name = "badge_name", length = 100, nullable = false)
    private String badgeName;

    @Column(name = "badge_info", length = 255)
    private String badgeInfo;

    @Column(name = "badge_icon", length = 255)
    private String badgeIcon;

    // ==== 비즈니스 메서드 ====
    public void updateBadgeInfo(String newBadgeInfo) {
        this.badgeInfo = newBadgeInfo;
    }

    public void updateBadgeIcon(String newBadgeIcon) {
        this.badgeIcon = newBadgeIcon;
    }

    public boolean hasBadgeInfo() {
        return badgeInfo != null && !badgeInfo.trim().isEmpty();
    }

    public boolean hasBadgeIcon() {
        return badgeIcon != null && !badgeIcon.trim().isEmpty();
    }

    public String getDisplayText() {
        if (hasBadgeInfo()) {
            return String.format("%s - %s", badgeName, badgeInfo);
        }
        return badgeName;
    }

    // 정적 팩토리 (카탈로그 정의 편의)
    public static Badge of(Integer badgeId, String badgeName, String badgeInfo, String badgeIcon) {
        return Badge.builder()
                .badgeId(badgeId)
                .badgeName(badgeName)
                .badgeInfo(badgeInfo)
                .badgeIcon(badgeIcon)
                .build();
    }
}