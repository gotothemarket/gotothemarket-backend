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

    // 비즈니스 메서드
    public void updateVibeName(String newVibeName) {
        this.vibeName = newVibeName;
    }

    // 바이브 이름이 특정 키워드를 포함하는지 확인
    public boolean containsKeyword(String keyword) {
        return vibeName != null && vibeName.toLowerCase().contains(keyword.toLowerCase());
    }

    // 바이브 타입 정보와 함께 표시용 문자열 생성
    public String getDisplayName() {
        return String.format("[%s] %s",
                vibeType != null ? vibeType.getVibeTypeName() : "미분류",
                vibeName);
    }
}