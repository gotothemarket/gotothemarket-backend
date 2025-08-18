// repository/BadgeRepository.java
package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    List<Badge> findByMember_MemberId(Integer memberId);
    int countByMember_MemberId(Integer memberId);
}