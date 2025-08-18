package com.example.gotothemarket.controller;

import com.example.gotothemarket.dto.CourseRequest;
import com.example.gotothemarket.dto.CourseResponse;
import com.example.gotothemarket.dto.KeywordTypeResponse;
import com.example.gotothemarket.service.KeywordService;
import com.example.gotothemarket.service.RecommendService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final RecommendService recommendService;
    private final KeywordService keywordService;

    public AiController(RecommendService recommendService, KeywordService keywordService) {
        this.recommendService = recommendService;
        this.keywordService = keywordService;
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> recommend(@Valid @RequestBody CourseRequest req) {
        return ResponseEntity.ok(recommendService.recommendCourses(req));
    }

    // 예) /api/ai/type?keyword=과일·야채
    @GetMapping("/type")
    public KeywordTypeResponse getTypes(@RequestParam("keyword") String storeTypeName) {
        return keywordService.getKeywordsByStoreTypeName(storeTypeName);
    }
}