package com.example.gotothemarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponse {
    private Long badgeId;
    private String badgeName;
    private String badgeInfo;
    private String badgeIcon;
    private boolean acquired;
    private boolean equipped;
}