// package: com.example.gotothemarket.service
package com.example.gotothemarket.service;

import com.example.gotothemarket.converter.CohereClassifier;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

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

import com.example.gotothemarket.entity.Vibe;
import com.example.gotothemarket.repository.VibeRepository;
import com.example.gotothemarket.service.VibeAggregationService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CohereClassifier cohereClassifier;
    private final VibeRepository vibeRepository;
    private final VibeAggregationService vibeAggregationService;
    private final BadgeService badgeService;
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
        // 리뷰 생성 후 뱃지 지급/업데이트 이벤트
        badgeService.onReviewCreated(saved);

        // ---- 멀티라벨 분석 (Cohere) ----
        double threshold = 0.60; // 기본 임계값. 필요 시 요청으로 받도록 확장 가능
        List<CohereClassifier.LabelScore> labels;
        try {
            labels = cohereClassifier.predict(req.content(), threshold);
        } catch (Exception e) {
            log.error("Cohere classify 실패: {}", e.getMessage(), e);
            labels = List.of();
        }

        // 분석 결과 로깅
        if (labels.isEmpty()) {
            log.info("[review:{} store:{}] 멀티라벨 없음 (threshold={})", saved.getReviewId(), storeId, threshold);
        } else {
            log.info("[review:{} store:{}] predicted_labels={} (threshold={})", saved.getReviewId(), storeId, labels, threshold);
        }

        // label_code("신선함" 등) -> Vibe 매핑, confidence를 가중치로 사용
        Map<Vibe, Double> picked = new HashMap<>();
        for (CohereClassifier.LabelScore item : labels) {
            Optional<Vibe> opt = vibeRepository.findByLabelCode(item.label);
            if (opt.isPresent()) {
                Vibe vibe = opt.get();
                picked.merge(vibe, item.confidence, Double::sum);
            } else {
                log.warn("모델 라벨을 Vibe로 매핑하지 못했습니다: label_code={}", item.label);
            }
        }

        // 비어 있어도 review_count는 증가해야 하므로 무조건 집계 호출
        vibeAggregationService.applyReviewCreated(store, picked);
        log.info("[review:{} store:{}] 집계 반영 완료. 라벨 수={} (threshold={})", saved.getReviewId(), storeId, picked.size(), threshold);

        return saved.getReviewId();
    }
}