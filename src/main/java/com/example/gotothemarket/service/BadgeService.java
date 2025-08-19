package com.example.gotothemarket.service;

import com.example.gotothemarket.badge.BadgeIds;
import com.example.gotothemarket.dto.BadgeResponse;
import com.example.gotothemarket.entity.Badge;
import com.example.gotothemarket.entity.UserBadge;
import com.example.gotothemarket.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gotothemarket.entity.CourseUsageLog;
import com.example.gotothemarket.entity.Review;
import java.time.Instant;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    // ★ 새로 주입
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final PhotoRepository photoRepository;
    private final CourseUsageLogRepository courseUsageLogRepository;

    public List<BadgeResponse> getUserBadges(Long memberId) {
        Integer mid = memberId == null ? null : memberId.intValue();
        if (mid == null) throw new IllegalArgumentException("memberId is required");
        List<Badge> allBadges = badgeRepository.findAll();
        List<UserBadge> userBadges = userBadgeRepository.findByMemberId(mid);

        Map<Integer, UserBadge> userBadgeMap = userBadges.stream()
                .collect(Collectors.toMap(ub -> ub.getBadgeId().intValue(), ub -> ub));

        return allBadges.stream()
                .map(badge -> {
                    UserBadge userBadge = userBadgeMap.get(badge.getBadgeId());
                    return BadgeResponse.builder()
                            .badgeId(Long.valueOf(badge.getBadgeId()))
                            .badgeName(badge.getBadgeName())
                            .badgeInfo(badge.getBadgeInfo())
                            .badgeIcon(badge.getBadgeIcon())
                            .acquired(userBadge != null && userBadge.isAcquired())
                            .equipped(userBadge != null && userBadge.isEquipped())
                            .build();
                }).collect(Collectors.toList());
    }

    /** 공통 지급 헬퍼 */
    private void grantIfNotAcquired(Integer memberId, Integer badgeId, boolean condition) {
        if (!condition) return;
        boolean has = userBadgeRepository.findByMemberIdAndBadgeId(memberId, badgeId).isPresent();
        if (!has) {
            userBadgeRepository.save(new UserBadge(memberId.longValue(), badgeId.longValue(), true, false));
        }
    }

    /** 1) 앱 첫 실행(설치 후 첫 진입 시 프론트에서 한 번만 호출) */
    @Transactional
    public void onFirstLaunch(Integer memberId) {
        grantIfNotAcquired(memberId, (int) BadgeIds.FIRST_LAUNCH, true);
    }

    /** 2) 가게 등록 직후 호출 */
    @Transactional
    public void onStoreCreated(Integer memberId) {
        long myStores = storeRepository.countByMember_MemberId(memberId);
        // 친절한 길잡이: 가게 등록을 3번 넘게 했을 시
        grantIfNotAcquired(memberId, (int) BadgeIds.KIND_GUIDE, myStores >= 3);
        // 숨겨진 가게 헌터: 가게 등록 10번 초과
        grantIfNotAcquired(memberId, (int) BadgeIds.HIDDEN_HUNTER, myStores >= 10);
    }

    /** 3) 코스추천 사용 직후 호출 (추천 API에서 호출) */
    @Transactional
    public void onCourseRecommended(Integer memberId) {
        courseUsageLogRepository.save(
                CourseUsageLog.builder()
                        .memberId(memberId)
                        .usedAt(Instant.now())
                        .build()
        );
        long used = courseUsageLogRepository.countByMemberId(memberId);
        // 친절한 길잡이: 코스추천 5회 이상 사용 조건도 만족하면 지급
        grantIfNotAcquired(memberId, (int) BadgeIds.KIND_GUIDE, used >= 5);
    }

    /** 4) 리뷰 작성 직후 호출 */
    @Transactional
    public void onReviewCreated(Review review) {
        // 리뷰 또는 회원 정보가 없으면 처리하지 않음
        if (review == null || review.getMember() == null || review.getMember().getMemberId() == null) {
            return;
        }
        Integer memberId = review.getMember().getMemberId();

        long totalReviews = reviewRepository.countByMember_MemberId(memberId);
        long firstReviews = reviewRepository.countFirstReviewsByMember(memberId);
        long distinctMarkets = reviewRepository.countDistinctMarketsReviewed(memberId);
        long photos = photoRepository.countByMember_MemberId(memberId);

        // 리뷰 개척가: 가게의 첫 리뷰를 3개 이상
        grantIfNotAcquired(memberId, (int) BadgeIds.PIONEER_3FR, firstReviews >= 3);

        // 시장 러버: 서로 다른 시장 3곳에서 최소 1개 리뷰
        grantIfNotAcquired(memberId, (int) BadgeIds.MARKET_LOVER, distinctMarkets >= 3);

        // 성실한 기록가: 리뷰 20개 + 사진 10개
        grantIfNotAcquired(memberId, (int) BadgeIds.DILIGENT, totalReviews >= 20 && photos >= 10);
    }

    /** 5) 사진 업로드 직후 호출 */
    @Transactional
    public void onPhotoUploaded(Integer memberId) {
        long photos = photoRepository.countByMember_MemberId(memberId);
        long totalReviews = reviewRepository.countByMember_MemberId(memberId);

        // 전설의 사진가: 사진 30건 이상
        grantIfNotAcquired(memberId, (int) BadgeIds.LEGEND_PHOTO, photos >= 30);

        // 성실한 기록가: 리뷰 20개 + 사진 10개 (사진이 조건을 만족하게 만들 수 있으므로 여기서도 체크)
        grantIfNotAcquired(memberId, (int) BadgeIds.DILIGENT, totalReviews >= 20 && photos >= 10);
    }



    @Transactional
    public void equipBadge(Long memberId, Long badgeId) {
        if (memberId == null || badgeId == null)
            throw new IllegalArgumentException("memberId and badgeId are required");

        Integer mid = memberId.intValue();
        Integer bid = badgeId.intValue();

        // 존재하지 않는 뱃지면 400 에러 유도 (GlobalExceptionHandler에서 캐치)
        boolean exists = badgeRepository.findById(bid).isPresent();
        if (!exists) {
            throw new IllegalArgumentException("존재하지 않는 뱃지입니다: " + bid);
        }

        // 1) 보유(획득) 여부 확인: 없거나(acquired=false)면 장착 불가 → 400
        var opt = userBadgeRepository.findByMemberIdAndBadgeId(mid, bid);
        if (opt.isEmpty() || !opt.get().isAcquired()) {
            throw new IllegalArgumentException("아직 획득하지 않은 뱃지입니다: " + bid);
        }

        // 2) 기존 장착 모두 해제
        userBadgeRepository.unequipAll(mid);

        // 3) 선택 뱃지 장착
        var ub = opt.get();
        ub.setEquipped(true);
        userBadgeRepository.save(ub);
    }
}