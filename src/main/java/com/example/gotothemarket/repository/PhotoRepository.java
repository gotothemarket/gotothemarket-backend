package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    long countByMember_MemberId(Integer memberId);
}
