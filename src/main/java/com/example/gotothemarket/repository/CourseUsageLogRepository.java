package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.CourseUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseUsageLogRepository extends JpaRepository<CourseUsageLog, Long> {
    long countByMemberId(Integer memberId);
}