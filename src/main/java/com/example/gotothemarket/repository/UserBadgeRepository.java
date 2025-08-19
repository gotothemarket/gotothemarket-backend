package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByMemberId(Integer memberId);
    Optional<UserBadge> findByMemberIdAndBadgeId(Integer memberId, Integer badgeId);

    long countByMemberId(Integer memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserBadge ub SET ub.equipped = false " +
            "WHERE ub.memberId = :memberId AND ub.equipped = true")
    int unequipAll(@Param("memberId") Integer memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserBadge ub SET ub.equipped = true " +
            "WHERE ub.memberId = :memberId AND ub.badgeId = :badgeId")
    int equipOne(@Param("memberId") Integer memberId, @Param("badgeId") Integer badgeId);
}