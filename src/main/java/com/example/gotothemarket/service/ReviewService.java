// package: com.example.gotothemarket.service
package com.example.gotothemarket.service;

import com.example.gotothemarket.dto.ReviewCreateRequest;
import com.example.gotothemarket.entity.Member;
import com.example.gotothemarket.entity.Review;
import com.example.gotothemarket.entity.Store;
import com.example.gotothemarket.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    @PersistenceContext
    private EntityManager em;
    private static final Long DEMO_USER_ID = 1L;

    @Transactional
    public Integer create(Long storeId, ReviewCreateRequest req) {
        Member member = em.find(Member.class, DEMO_USER_ID);
        if (member == null) {
            throw new IllegalStateException("데모 사용자(" + DEMO_USER_ID + ")가 존재하지 않습니다. 더미 데이터를 먼저 삽입하세요.");
        }

        Store store = em.find(Store.class, storeId);
        if (store == null) {
            throw new IllegalArgumentException("존재하지 않는 가게입니다: " + storeId);
        }

        Review review = Review.builder()
                .member(member)
                .store(store)
                .rating(req.rating())
                .content(req.content())
                .build();

        Review saved = reviewRepository.save(review);
        return saved.getReviewId();
    }
}