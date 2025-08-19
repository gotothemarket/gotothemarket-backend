package com.example.gotothemarket.repository;

import com.example.gotothemarket.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReviewRepository extends JpaRepository<Review, Integer> {
    int countByMember_MemberId(Integer memberId);
    @Query(
            value = """
      SELECT COUNT(*) FROM (
        SELECT r.store_id
        FROM review r
        JOIN (
          SELECT store_id, MIN(created_at) AS first_time
          FROM review
          GROUP BY store_id
        ) fr ON fr.store_id = r.store_id AND fr.first_time = r.created_at
        WHERE r.member_id = :memberId
      ) t
      """,
            nativeQuery = true
    )
    long countFirstReviewsByMember(@Param("memberId") Integer memberId);

    @Query(
            value = """
      SELECT COUNT(DISTINCT s.market_id)
      FROM review r
      JOIN store s ON s.store_id = r.store_id
      WHERE r.member_id = :memberId
      """,
            nativeQuery = true
    )
    long countDistinctMarketsReviewed(@Param("memberId") Integer memberId);

    @Query(value = """
        SELECT m.market_name AS marketName,
               s.store_name  AS storeName,
               r.content     AS content
        FROM review r
        JOIN store s   ON s.store_id = r.store_id
        JOIN market m  ON m.market_id = s.market_id
        WHERE r.member_id = :memberId
        ORDER BY r.created_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM review r
        WHERE r.member_id = :memberId
        """,
            nativeQuery = true)
    Page<MyReviewProjection> findMyReviews(@Param("memberId") Integer memberId, Pageable pageable);
}