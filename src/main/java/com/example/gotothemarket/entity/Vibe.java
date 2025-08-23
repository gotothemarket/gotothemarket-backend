package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vibe")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vibe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vibe_id")
    private Integer vibeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vibe_type_id", nullable = false)
    private VibeType vibeType;

    @Column(name = "vibe_name", length = 100, nullable = false)
    private String vibeName;

    @Column(name = "label_code", length = 50, nullable = false, unique = true)
    private String labelCode;

    @Column(name = "code", nullable = false, unique = true)
    private Integer code;
}