package com.example.gotothemarket.controller;

import com.example.gotothemarket.service.StoreService;
import com.example.gotothemarket.dto.StoreDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "상점", description = "상점 관련 API")
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "가게 정보 등록")
    @PostMapping
    public ResponseEntity<StoreDTO.StoreResponseDTO> createStore(@RequestBody StoreDTO.StoreRequestDTO dto) {
        StoreDTO.StoreResponseDTO response = storeService.createStore(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 상세 조회")
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDTO.StoreDetailResponse> getStoreDetail(@PathVariable Integer storeId) {
        StoreDTO.StoreDetailResponse response = storeService.getStoreDetail(storeId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 정보 수정")
    @PatchMapping("/{storeId}")
    public ResponseEntity<StoreDTO.StoreDetailResponse> updateStore(
            @PathVariable Integer storeId,
            @RequestBody StoreDTO.StoreUpdateDTO updateDTO) {

        StoreDTO.StoreDetailResponse response = storeService.updateStore(storeId, updateDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 사진 업로드")
    @PostMapping(value = "/{storeId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoreDTO.PhotoUploadResponse> uploadStorePhoto(
            @PathVariable Integer storeId,
            @RequestParam("file") MultipartFile file) {

        StoreDTO.PhotoUploadResponse response = storeService.uploadStorePhoto(storeId, file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 사진 삭제")
    @DeleteMapping("/{storeId}/photo/{photoId}")
    public ResponseEntity<StoreDTO.PhotoDeleteResponse> deleteStorePhoto(
            @PathVariable Integer storeId,
            @PathVariable Integer photoId) {

        StoreDTO.PhotoDeleteResponse response = storeService.deleteStorePhoto(storeId, photoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 위치 검증 API")
    @GetMapping("/validate-location")
    public ResponseEntity<StoreDTO.LocationValidationResponse> validateStoreLocation(
            @RequestParam("lat") Double latitude,
            @RequestParam("lng") Double longitude) {

        StoreDTO.LocationValidationResponse response = storeService.validateStoreLocation(latitude, longitude);
        return ResponseEntity.ok(response);
    }


}