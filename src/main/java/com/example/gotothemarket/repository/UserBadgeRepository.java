package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    // 기존 메서드들 (하위 호환성 유지)
    List<UserBadge> findByMemberId(Integer memberId);
    Optional<UserBadge> findByMemberIdAndBadgeId(Integer memberId, Integer badgeId);

    // 새로 추가: Long 타입 지원 (실제 DB 타입과 일치)
    @Query("SELECT ub FROM UserBadge ub WHERE ub.memberId = :memberId")
    List<UserBadge> findByMemberIdLong(@Param("memberId") Long memberId);

    @Query("SELECT ub FROM UserBadge ub WHERE ub.memberId = :memberId AND ub.badgeId = :badgeId")
    Optional<UserBadge> findByMemberIdAndBadgeIdLong(@Param("memberId") Long memberId, @Param("badgeId") Long badgeId);

    long countByMemberId(Integer memberId);

    // Long 버전 추가
    @Query("SELECT COUNT(ub) FROM UserBadge ub WHERE ub.memberId = :memberId")
    long countByMemberIdLong(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserBadge ub SET ub.equipped = false " +
            "WHERE ub.memberId = :memberId AND ub.equipped = true")
    int unequipAll(@Param("memberId") Integer memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserBadge ub SET ub.equipped = true " +
            "WHERE ub.memberId = :memberId AND ub.badgeId = :badgeId")
    int equipOne(@Param("memberId") Integer memberId, @Param("badgeId") Integer badgeId);
}