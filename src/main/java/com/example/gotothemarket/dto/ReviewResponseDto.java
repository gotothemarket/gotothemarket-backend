package com.example.gotothemarket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long reviewId,
        String marketName,
        String storeName,
        BigDecimal rating,
        String content,
        LocalDateTime createdAt
) {}