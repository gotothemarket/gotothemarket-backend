package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    // Badge는 마스터 테이블입니다. member 관련 메소드는 여기 두지 않습니다.
}