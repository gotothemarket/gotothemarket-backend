package com.example.gotothemarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class KeywordTypeResponse {
    public boolean success;
    public int status;
    public Data data;

    public KeywordTypeResponse(boolean success, int status, Data data) {
        this.success = success; this.status = status; this.data = data;
    }

    public static class Data {
        @JsonProperty("store_type") public StoreTypeInfo storeType;
        public List<Group> groups;
        public Data(StoreTypeInfo storeType, List<Group> groups) {
            this.storeType = storeType; this.groups = groups;
        }
    }

    public static class StoreTypeInfo {
        @JsonProperty("id")   public int id;
        @JsonProperty("name") public String name;
        public StoreTypeInfo(int id, String name) { this.id = id; this.name = name; }
    }

    public static class Group {
        @JsonProperty("vibe_type_id")   public int vibeTypeId;
        @JsonProperty("vibe_type_name") public String vibeTypeName;
        public List<Keyword> keywords;
        public Group(int vibeTypeId, String vibeTypeName, List<Keyword> keywords) {
            this.vibeTypeId = vibeTypeId; this.vibeTypeName = vibeTypeName; this.keywords = keywords;
        }
    }

    public static class Keyword {
        public int code;                              // 101, 102 ...
        @JsonProperty("label_code") public String labelCode; // "맛있음"
        @JsonProperty("display")    public String display;   // "맛이 좋아요"
        public Keyword(int code, String labelCode, String display) {
            this.code = code; this.labelCode = labelCode; this.display = display;
        }
    }
}