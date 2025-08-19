package com.example.gotothemarket.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BadgeResponse {
    private Long badgeId;
    private String badgeName;
    private String badgeInfo;
    private String badgeIcon;
    private boolean acquired;
    private boolean equipped;
}