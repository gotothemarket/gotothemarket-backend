package com.example.gotothemarket.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ReviewCreateRequest(
        @NotNull
        @DecimalMin(value = "0.0") @DecimalMax(value = "5.0")
        @Digits(integer = 2, fraction = 1) // 예: 0.0 ~ 10.0 형태 허용, 우리 스키마는 (2,1)
        BigDecimal rating,

        @Size(max = 500) // 엔티티 content 길이와 일치
        String content
) {}