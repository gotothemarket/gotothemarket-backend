package com.example.gotothemarket.controller;

import com.example.gotothemarket.dto.CourseRequest;
import com.example.gotothemarket.dto.CourseResponse;
import com.example.gotothemarket.dto.KeywordTypeResponse;
import com.example.gotothemarket.service.KeywordService;
import com.example.gotothemarket.service.RecommendService;
import com.example.gotothemarket.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "AI API")
public class AiController {

    private final RecommendService recommendService;
    private final KeywordService keywordService;
    private final BadgeService badgeService;

    public AiController(RecommendService recommendService, KeywordService keywordService, BadgeService badgeService) {
        this.recommendService = recommendService;
        this.keywordService = keywordService;
        this.badgeService = badgeService;
    }

    @Operation(summary = "AI 코스 추천")
    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> recommend(@RequestParam("memberId") Integer memberId,
                                                   @Valid @RequestBody CourseRequest req) {
        CourseResponse response = recommendService.recommendCourses(req);
        try {
            // 코스 추천 사용 시 배지 조건 체크 (예: '친절한 길잡이'의 코스 추천 5회 조건)
            badgeService.onCourseRecommended(memberId);
        } catch (Exception ignored) {
            // 배지 처리 실패가 코스 추천 응답을 막지 않도록 방어
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "AI 코스 추천 - 타입")
    // 예) /api/ai/type?keyword=과일·야채
    @GetMapping("/type")
    public KeywordTypeResponse getTypes(@RequestParam("keyword") String storeTypeName) {
        return keywordService.getKeywordsByStoreTypeName(storeTypeName);
    }
}