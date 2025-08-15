package com.example.gotothemarket.controller;

import com.example.gotothemarket.service.StoreService;
import com.example.gotothemarket.dto.StoreDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// StoreController.java
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    //가게ㅔ 정보 등록
    @PostMapping
    public ResponseEntity<StoreDTO.StoreResponseDTO> createStore(@RequestBody StoreDTO.StoreRequestDTO dto) {
        StoreDTO.StoreResponseDTO response = storeService.createStore(dto);
        return ResponseEntity.ok(response);
    }
    // 단일 상점 상세 조회
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDTO.StoreDetailResponse> getStoreDetail(@PathVariable Integer storeId) {
        StoreDTO.StoreDetailResponse response = storeService.getStoreDetail(storeId);
        return ResponseEntity.ok(response);
    }
    // 상점 정보 부분 업데이트
    @PatchMapping("/{storeId}")
    public ResponseEntity<StoreDTO.StoreDetailResponse> updateStore(
            @PathVariable Integer storeId,
            @RequestBody StoreDTO.StoreUpdateDTO updateDTO) {

        StoreDTO.StoreDetailResponse response = storeService.updateStore(storeId, updateDTO);
        return ResponseEntity.ok(response);
    }
}
