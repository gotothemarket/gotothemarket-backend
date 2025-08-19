package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_badge",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id","badge_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserBadge {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "badge_id", nullable = false)
    private Long badgeId;

    @Column(nullable = false)
    private boolean acquired;

    @Column(nullable = false)
    private boolean equipped;

    public UserBadge(Long memberId, Long badgeId, boolean acquired, boolean equipped) {
        this.memberId = memberId;
        this.badgeId = badgeId;
        this.acquired = acquired;
        this.equipped = equipped;
    }
}