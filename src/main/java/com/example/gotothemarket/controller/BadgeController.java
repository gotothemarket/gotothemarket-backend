package com.example.gotothemarket.controller;

import com.example.gotothemarket.dto.BadgeResponse;
import com.example.gotothemarket.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@Tag(name = "뱃지", description = "뱃지 API")
public class BadgeController {
    private final BadgeService badgeService;

    @GetMapping
    @Operation(summary = "뱃지 조회")
    public ResponseEntity<List<BadgeResponse>> getUserBadges(@RequestParam("memberId") Long memberId) {
        return ResponseEntity.ok(badgeService.getUserBadges(memberId));
    }

    @PostMapping("/equip")
    @Operation(summary = "뱃지 장착")
    public ResponseEntity<Map<String, Object>> equipBadge(@RequestParam("memberId") Long memberId,
                                                          @RequestParam("badgeId") Long badgeId) {
        // 서비스에서 유효성 체크 + 장착 처리 (획득 안했으면 IllegalArgumentException -> 전역 핸들러가 400 반환)
        badgeService.equipBadge(memberId, badgeId);

        Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("message", "뱃지를 장착했습니다.");
        payload.put("member_id", memberId);
        payload.put("badge_id", badgeId);
        payload.put("equipped", true);

        return ResponseEntity.ok(payload);
    }
}