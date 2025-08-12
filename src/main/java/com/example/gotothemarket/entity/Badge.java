package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badge")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Integer badgeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "badge_name", length = 100, nullable = false)
    private String badgeName;

    @Column(name = "badge_info", length = 255, nullable = true)
    private String badgeInfo;

    @Column(name = "badge_icon", length = 255, nullable = true)
    private String badgeIcon;

    // 비즈니스 메서드
    public void updateBadgeInfo(String newBadgeInfo) {
        this.badgeInfo = newBadgeInfo;
    }

    public void updateBadgeIcon(String newBadgeIcon) {
        this.badgeIcon = newBadgeIcon;
    }

    // 배지 정보가 있는지 확인
    public boolean hasBadgeInfo() {
        return badgeInfo != null && !badgeInfo.trim().isEmpty();
    }

    // 배지 아이콘이 있는지 확인
    public boolean hasBadgeIcon() {
        return badgeIcon != null && !badgeIcon.trim().isEmpty();
    }

    // 표시용 문자열 생성
    public String getDisplayText() {
        if (hasBadgeInfo()) {
            return String.format("%s - %s", badgeName, badgeInfo);
        }
        return badgeName;
    }

    // 정적 팩토리 메서드 - 배지 생성
    public static Badge createBadge(Member member, String badgeName, String badgeInfo) {
        return Badge.builder()
                .member(member)
                .badgeName(badgeName)
                .badgeInfo(badgeInfo)
                .build();
    }
}