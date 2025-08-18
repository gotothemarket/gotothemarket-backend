package com.example.gotothemarket.service;

import com.example.gotothemarket.dto.MyPageResponse;
import com.example.gotothemarket.entity.Badge;
import com.example.gotothemarket.entity.Member;
import com.example.gotothemarket.repository.*;
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

    public MyPageService(MemberRepository memberRepository,
                         StoreRepository storeRepository,
                         ReviewRepository reviewRepository,
                         BadgeRepository badgeRepository) {
        this.memberRepository = memberRepository;
        this.storeRepository = storeRepository;
        this.reviewRepository = reviewRepository;
        this.badgeRepository = badgeRepository;
    }

    public MyPageResponse getMyPage(Integer memberId){
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + memberId));

        int storeCnt  = storeRepository.countByMember_MemberId(memberId);
        int reviewCnt = reviewRepository.countByMember_MemberId(memberId);
        int badgeCnt  = badgeRepository.countByMember_MemberId(memberId);

        // 장착 배지 1개만 노출(없으면 빈 배열)
        List<Badge> badges = badgeRepository.findByMember_MemberId(memberId);
        Optional<Badge> attached = Optional.empty();
        if (m.getAttachedBadgeId() != null) {
            attached = badges.stream()
                    .filter(b -> b.getBadgeId().equals(m.getAttachedBadgeId()))
                    .findFirst();
        }

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