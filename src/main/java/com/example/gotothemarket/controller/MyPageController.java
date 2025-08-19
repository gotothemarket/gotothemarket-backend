package com.example.gotothemarket.controller;

import com.example.gotothemarket.dto.MyPageResponse;
import com.example.gotothemarket.service.MyPageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    // 임시로 memberId 쿼리 파라미터 사용 (인증 붙으면 Security의 사용자ID로 대체)
    @GetMapping
    public MyPageResponse get(@RequestParam("memberId") Integer memberId) {
        return myPageService.getMyPage(memberId);
    }

    @GetMapping("/favorite")
    public Object getFavorites(@RequestParam("memberId") Integer memberId,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size) {
        return myPageService.getFavorites(memberId, page, size);
    }
}