package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="store_vibe_stat")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class StoreVibeStat {

    @EmbeddedId
    private StoreVibeKey id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("storeId")
    @JoinColumn(name="store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("vibeId")
    @JoinColumn(name="vibe_id")
    private Vibe vibe;

    @Column(name="hit_count", nullable=false)
    private double hitCount; // = 발생 횟수 또는 confidence 합

    @Column(name="ratio", nullable=false)
    private double ratio;    // = hit_count / keyword_total (스무딩 반영 가능)

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;

    public void addHit(double w){ this.hitCount += w; }
    public void setRatio(double r){ this.ratio = r; }
    public void touch(){ this.updatedAt = LocalDateTime.now(); }
}