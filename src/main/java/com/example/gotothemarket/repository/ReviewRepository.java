package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    int countByMember_MemberId(Integer memberId);
}