package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "store_type", length = 50, nullable = true)
    private String storeType;

    @Column(name = "rating", precision = 2, scale = 1, nullable = false)
    private BigDecimal rating;

    @Column(name = "content", length = 500, nullable = true)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 생성 시점에 자동으로 현재 시간 설정
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}