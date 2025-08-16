package com.example.gotothemarket.controller;

import com.example.gotothemarket.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "상점 관련 API")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{storeId}/toggle-favorite")
    @Operation(summary = "즐겨찾기 토글", description = "즐겨찾기를 추가하거나 제거합니다.")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @Parameter(description = "상점 ID") @PathVariable Integer storeId) {

        try {
            boolean isAdded = favoriteService.toggleFavorite(storeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isFavorite", isAdded);
            response.put("message", isAdded ? "즐겨찾기에 추가되었습니다." : "즐겨찾기에서 제거되었습니다.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}