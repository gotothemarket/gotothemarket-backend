package com.example.gotothemarket.controller;

import com.example.gotothemarket.service.StoreService;
import com.example.gotothemarket.dto.StoreDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// StoreController.java
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreDTO.StoreResponseDTO> createStore(@RequestBody StoreDTO.StoreRequestDTO dto) {
        StoreDTO.StoreResponseDTO response = storeService.createStore(dto);
        return ResponseEntity.ok(response);
    }
}
