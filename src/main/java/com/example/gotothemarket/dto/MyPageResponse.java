package com.example.gotothemarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MyPageResponse(
        boolean success,
        int status,
        Data data
) {
    public static MyPageResponse ok(Data data){
        return new MyPageResponse(true, 200, data);
    }

    public record Data(Profile profile) {}

    public record Profile(
            @JsonProperty("member_id") Integer memberId,
            String nickname,
            List<BadgeItem> badges,          // 장착(대표) 배지만 0~1개 노출
            @JsonProperty("store_count")  int storeCount,
            @JsonProperty("review_count") int reviewCount,
            @JsonProperty("badge_count")  int badgeCount
    ) {}

    public record BadgeItem(
            @JsonProperty("attached_badge_id") Integer attachedBadgeId,
            @JsonProperty("badge_name") String badgeName,
            @JsonProperty("badge_icon") String badgeIcon
    ) {}
}