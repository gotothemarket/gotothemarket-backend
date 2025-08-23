package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vibe_type")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VibeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vibe_type_id")
    private Integer vibeTypeId;

    @Column(name = "store_type", nullable = false)
    private Integer storeType;

    @Column(name = "vibe_type_name", length = 50, nullable = false)
    private String vibeTypeName;

    // Vibe와의 1:N 관계 (한 바이브 타입에 여러 바이브)
    @OneToMany(mappedBy = "vibeType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Vibe> vibes = new ArrayList<>();
}