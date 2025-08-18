package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "photo")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Integer photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = true)  // nullable = true 추가
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "photo_url", length = 500, nullable = false)
    private String photoUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 생성 시점에 자동으로 현재 시간 설정
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // 비즈니스 메서드들은 그대로 유지
    public void updatePhotoUrl(String newPhotoUrl) {
        this.photoUrl = newPhotoUrl;
    }

    public boolean isValidUrl() {
        return photoUrl != null &&
                (photoUrl.startsWith("http://") ||
                        photoUrl.startsWith("https://") ||
                        photoUrl.startsWith("/uploads/"));
    }

    public boolean isImageFile() {
        if (photoUrl == null) return false;
        String lowerUrl = photoUrl.toLowerCase();
        return lowerUrl.endsWith(".jpg") ||
                lowerUrl.endsWith(".jpeg") ||
                lowerUrl.endsWith(".png") ||
                lowerUrl.endsWith(".gif") ||
                lowerUrl.endsWith(".webp");
    }

    public String getFileName() {
        if (photoUrl == null) return null;
        int lastSlash = photoUrl.lastIndexOf('/');
        return lastSlash >= 0 ? photoUrl.substring(lastSlash + 1) : photoUrl;
    }
}