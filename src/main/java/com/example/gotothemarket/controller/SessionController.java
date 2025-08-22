package com.example.gotothemarket.controller;

import com.example.gotothemarket.service.BadgeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/session")
@Tag(name = "앱 시작", description = "앱 시작 관련 API")
public class SessionController {
    private final BadgeService badgeService;

    @PostMapping("/first-launch")
    public ResponseEntity<Map<String, Object>> firstLaunch(@RequestParam Integer memberId) {
        // 1) 배지 지급 로직 실행 (서비스 내부에서 중복 지급 방지 처리)
        badgeService.onFirstLaunch(memberId);

        // 2) 프론트가 바로 토스트/알림을 띄울 수 있도록 응답 페이로드 구성
        Map<String, Object> data = new HashMap<>();
        data.put("awarded", true);
        data.put("equipped", true);        // 첫 배지는 기본 장착했다고 가정(서비스 정책에 맞게 조정 가능)
        data.put("badge_id", 1);           // '용감한 첫 발자국'의 고정 ID
        data.put("badge_name", "용감한 첫 발자국");
        data.put("badge_info", "앱 설치 후 첫 실행 시 자동 지급");
        data.put("badge_icon", "https://gotothemarket-bucket.s3.ap-northeast-2.amazonaws.com/badge-icons/badge_1_icon.png");

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("status", 200);
        body.put("data", data);

        return ResponseEntity.ok(body);
    }
}