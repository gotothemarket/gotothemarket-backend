// package: com.example.gotothemarket.controller
package com.example.gotothemarket.controller;

import com.example.gotothemarket.dto.ApiMessageResponse;
import com.example.gotothemarket.dto.ReviewCreateRequest;
import com.example.gotothemarket.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/store/{storeId}/review")
    public ResponseEntity<?> createReview(
            @PathVariable Long storeId,
            @Valid @RequestBody ReviewCreateRequest req) {

        reviewService.create(storeId, req);
        return ResponseEntity.status(201).body(
                Map.of("success", true, "message", "리뷰가 성공적으로 등록되었습니다.", "status", 201)
        );
    }
}