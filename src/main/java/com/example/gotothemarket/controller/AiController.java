package com.example.gotothemarket.controller;

import com.example.gotothemarket.dto.CourseRequest;
import com.example.gotothemarket.dto.CourseResponse;
import com.example.gotothemarket.service.RecommendService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final RecommendService recommendService;

    public AiController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> recommend(@Valid @RequestBody CourseRequest req) {
        return ResponseEntity.ok(recommendService.recommendCourses(req));
    }
}