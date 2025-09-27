package com.example.gotothemarket.service;

import com.example.gotothemarket.dto.MyPageFavoriteResponse;
import com.example.gotothemarket.dto.MyPageResponse;
import com.example.gotothemarket.entity.Badge;
import com.example.gotothemarket.entity.Favorite;
import com.example.gotothemarket.entity.Member;
import com.example.gotothemarket.entity.UserBadge;
import com.example.gotothemarket.repository.*;
import com.example.gotothemarket.repository.UserBadgeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class MyPageService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final FavoriteRepository favoriteRepository;
    private final S3Service s3Service;

    public MyPageService(MemberRepository memberRepository,
                         StoreRepository storeRepository,
                         ReviewRepository reviewRepository,
                         BadgeRepository badgeRepository,
                         UserBadgeRepository userBadgeRepository,
                         FavoriteRepository favoriteRepository,
                         S3Service s3Service) {
        this.memberRepository = memberRepository;
        this.storeRepository = storeRepository;
        this.reviewRepository = reviewRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.favoriteRepository = favoriteRepository;
        this.s3Service = s3Service;
    }

    @Cacheable(value = "favorites", key = "#memberId + '-' + #page + '-' + #size")
    public MyPageFavoriteResponse getFavorites(Integer memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Favorite> favoritesPage = favoriteRepository.findByMember_MemberId(memberId, pageable);

        List<MyPageFavoriteResponse.FavoriteDto> favorites = favoritesPage.getContent().stream()
                .map(fav -> new MyPageFavoriteResponse.FavoriteDto(
                        fav.getStore().getStoreId(),
                        fav.getStore().getStoreName(),
                        fav.getStore().getMarket().getMarketName(),
                        s3Service.getStoreTypeIconUrl(fav.getStore().getStoreType().getStoreTypeId())
                ))
                .toList();

        return MyPageFavoriteResponse.builder()
                .success(true)
                .status(200)
                .data(MyPageFavoriteResponse.Data.builder()
                        .favorites(favorites)
                        .page(page)
                        .size(size)
                        .total(favoritesPage.getTotalElements())
                        .build())
                .build();
    }

    // MyPageService.java
    @Transactional(readOnly = true)
    public Map<String, Object> getMyReviews(Integer memberId, int page, int size) {
        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);

        Page<MyReviewProjection> p = reviewRepository.findMyReviews(memberId, pageable);

        List<LinkedHashMap<String, Object>> reviews = p.getContent().stream()
                .map(row -> {
                    var item = new LinkedHashMap<String, Object>();
                    item.put("store_id",    row.getStoreId());   // null 가능성이 있으면 Optional 처리 or 기본값
                    item.put("market_name", row.getMarketName());
                    item.put("store_name",  row.getStoreName());
                    item.put("content",     row.getContent());
                    return item;
                })
                .toList();

        return Map.of(
                "success", true,
                "status", 200,
                "data", Map.of(
                        "reviews", reviews,
                        "page", page,
                        "size", size,
                        "total", p.getTotalElements()
                )
        );
    }

    public MyPageResponse getMyPage(Integer memberId){
        try {
            Member m = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + memberId));

            int storeCnt = storeRepository.countByMember_MemberId(memberId);
            int reviewCnt = reviewRepository.countByMember_MemberId(memberId);

            // 기존 Integer 타입 메서드 사용 (이미 작동하는 메서드)
            List<UserBadge> userBadges = userBadgeRepository.findByMemberId(memberId);
            int badgeCnt = (int) userBadges.stream().filter(UserBadge::isAcquired).count();

            // 장착된 배지 찾기 (equipped=true인 배지)
            Optional<Integer> attachedBadgeIdOpt = userBadges.stream()
                    .filter(UserBadge::isEquipped)
                    .map(ub -> {
                        // 안전한 타입 변환 - Long을 Integer로 변환
                        Long badgeId = ub.getBadgeId();
                        return badgeId != null ? badgeId.intValue() : null;
                    })
                    .filter(Objects::nonNull) // null 값 제거
                    .findFirst();

            // Member의 attachedBadgeId 백업 사용 (현재는 NULL)
            if (attachedBadgeIdOpt.isEmpty() && m.getAttachedBadgeId() != null) {
                attachedBadgeIdOpt = Optional.of(m.getAttachedBadgeId());
            }

            // Badge 상세 정보 조회
            var badgeItems = List.<MyPageResponse.BadgeItem>of();
            if (attachedBadgeIdOpt.isPresent()) {
                final Integer finalBadgeId = attachedBadgeIdOpt.get(); // final 변수로 추출
                Optional<Badge> attached = badgeRepository.findById(finalBadgeId);
                badgeItems = attached
                        .map(b -> List.of(new MyPageResponse.BadgeItem(
                                finalBadgeId, // attachedBadgeIdOpt.get() 대신 finalBadgeId 사용
                                b.getBadgeName(),
                                b.getBadgeIcon())))
                        .orElse(List.of());
            }

            var profile = new MyPageResponse.Profile(
                    m.getMemberId(),
                    m.getNickname(),
                    badgeItems,
                    storeCnt,
                    reviewCnt,
                    badgeCnt
            );

            return MyPageResponse.ok(new MyPageResponse.Data(profile));

        } catch (Exception e) {
            System.err.println("MyPage Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("마이페이지 조회 중 오류가 발생했습니다", e);
        }
    }
}