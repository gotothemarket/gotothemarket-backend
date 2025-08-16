// package: com.example.gotothemarket.controller
package com.example.gotothemarket.controller;

import com.example.gotothemarket.dto.ApiMessageResponse;
import com.example.gotothemarket.dto.ReviewCreateRequest;
import com.example.gotothemarket.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "상점", description = "상점 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록")
    @PostMapping("/stores/{storeId}/review")
    public ResponseEntity<?> createReview(
            @PathVariable Long storeId,
            @Valid @RequestBody ReviewCreateRequest req) {

        reviewService.create(storeId, req);
        return ResponseEntity.status(201).body(
                Map.of("success", true, "message", "리뷰가 성공적으로 등록되었습니다.", "status", 201)
        );
    }
}