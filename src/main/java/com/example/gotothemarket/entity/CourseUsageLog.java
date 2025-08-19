package com.example.gotothemarket.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "course_usage_log", indexes = {
        @Index(name="idx_course_usage_member", columnList = "member_id")
})
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseUsageLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="member_id", nullable=false)
    private Integer memberId;

    @Column(name="used_at", nullable=false)
    private Instant usedAt;
}