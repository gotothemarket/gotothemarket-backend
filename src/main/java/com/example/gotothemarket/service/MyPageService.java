package com.example.gotothemarket.service;

import com.example.gotothemarket.dto.MyPageResponse;
import com.example.gotothemarket.entity.Badge;
import com.example.gotothemarket.entity.Member;
import com.example.gotothemarket.entity.UserBadge;
import com.example.gotothemarket.repository.*;
import com.example.gotothemarket.repository.UserBadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MyPageService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public MyPageService(MemberRepository memberRepository,
                         StoreRepository storeRepository,
                         ReviewRepository reviewRepository,
                         BadgeRepository badgeRepository,
                         UserBadgeRepository userBadgeRepository) {
        this.memberRepository = memberRepository;
        this.storeRepository = storeRepository;
        this.reviewRepository = reviewRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    public MyPageResponse getMyPage(Integer memberId){
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + memberId));

        int storeCnt  = storeRepository.countByMember_MemberId(memberId);
        int reviewCnt = reviewRepository.countByMember_MemberId(memberId);
        // 사용자 보유(획득) 뱃지 수: acquired=true만 카운트
        List<UserBadge> userBadges = userBadgeRepository.findByMemberId(memberId);
        int badgeCnt = (int) userBadges.stream().filter(UserBadge::isAcquired).count();

        // 장착된 배지 1개만 노출: user_badge.equipped=true 우선, 없으면 member.attachedBadgeId 사용
        Optional<Integer> attachedBadgeIdOpt = userBadges.stream()
                .filter(UserBadge::isEquipped)
                .map(ub -> ub.getBadgeId().intValue())
                .findFirst();

        if (attachedBadgeIdOpt.isEmpty() && m.getAttachedBadgeId() != null) {
            attachedBadgeIdOpt = Optional.of(m.getAttachedBadgeId());
        }

        Optional<Badge> attached = attachedBadgeIdOpt
                .flatMap(id -> badgeRepository.findById(id));

        var badgeItems = attached
                .map(b -> List.of(new MyPageResponse.BadgeItem(
                        m.getAttachedBadgeId(), b.getBadgeName(), b.getBadgeIcon())))
                .orElse(List.of());

        var profile = new MyPageResponse.Profile(
                m.getMemberId(),
                m.getNickname(),
                badgeItems,
                storeCnt,
                reviewCnt,
                badgeCnt
        );
        return MyPageResponse.ok(new MyPageResponse.Data(profile));
    }
}