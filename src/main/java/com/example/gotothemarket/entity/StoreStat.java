package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="store_stat")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStat {
    @Id
    @Column(name="store_id")
    private Integer storeId; // Store FK (1:1)

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name="store_id")
    private Store store;

    @Column(name="review_count", nullable=false)
    private int reviewCount;

    @Column(name="keyword_total", nullable=false)
    private double keywordTotal; // 가중치(=confidence 합)도 지원하려면 double 권장

    @Column(name="last_aggregated", nullable=false)
    private LocalDateTime lastAggregated;

    public void incReviews(int delta){ this.reviewCount += delta; }
    public void incKeywordTotal(double delta){ this.keywordTotal += delta; }
    public void touch(){ this.lastAggregated = LocalDateTime.now(); }
}