package com.example.gotothemarket.controller;

import com.example.gotothemarket.dto.MyPageResponse;
import com.example.gotothemarket.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@Tag(name = "마이페이지", description = "마이페이지 관련 API")
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    // 임시로 memberId 쿼리 파라미터 사용 (인증 붙으면 Security의 사용자ID로 대체)
    @GetMapping
    @Operation(summary = "마이페이지 조회")
    public MyPageResponse get(@RequestParam("memberId") Integer memberId) {
        return myPageService.getMyPage(memberId);
    }

    @GetMapping("/favorite")
    @Operation(summary = "마이페이지 즐겨찾기 조회")
    public Object getFavorites(@RequestParam("memberId") Integer memberId,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size) {
        return myPageService.getFavorites(memberId, page, size);
    }
    @GetMapping("/review")
    @Operation(summary = "마이페이지 리뷰 조회")
    public Object getMyReviews(@RequestParam("memberId") Integer memberId,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size) {
        return myPageService.getMyReviews(memberId, page, size);
    }
}