package com.example.gotothemarket.service;

import com.example.gotothemarket.dto.CourseRequest;
import com.example.gotothemarket.dto.CourseResponse;
import com.example.gotothemarket.dto.TopStoreDto;
import com.example.gotothemarket.entity.Vibe;
import com.example.gotothemarket.repository.RecommendRepository;
import com.example.gotothemarket.repository.VibeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendService {

    private final RecommendRepository recommendRepository;
    private final VibeRepository vibeRepository;

    public RecommendService(RecommendRepository recommendRepository, VibeRepository vibeRepository) {
        this.recommendRepository = recommendRepository;
        this.vibeRepository = vibeRepository;
    }

    public CourseResponse recommendCourses(CourseRequest req) {
        List<CourseResponse.Course> courses = new ArrayList<>();

        for (int i = 0; i < req.getSets().size(); i++) {
            var set = req.getSets().get(i);

            // 1) 키워드 → label_code 로 유연 매핑
            List<String> labels = toLabelCodes(set.getKeywords());
            if (labels.isEmpty()) continue; // 매칭 실패 시 스킵(원하시면 400 처리로 바꿔도 됩니다)

            // 2) 시장 + 업종에서 labels의 ratio 합이 최대인 가게 1곳
            TopStoreDto top = recommendRepository
                    .pickTopStoreByLabels(req.getMarketId(), set.getStoreType(), labels);

            if (top != null) {
                courses.add(new CourseResponse.Course(
                        i + 1,
                        top.getStoreId(),
                        top.getStoreName(),
                        top.getStoreType(),
                        set.getKeywords() // 사용자가 요청한 원문 키워드 그대로 반환
                ));
            }
        }

        return new CourseResponse(true, 200, new CourseResponse.Data(courses));
    }

    /** 문장/label_code/숫자코드(101 등)를 모두 label_code로 변환 */
    private List<String> toLabelCodes(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return List.of();

        // vibe 전체 로딩 후 맵 구성 (쿼리 N번 방지)
        List<Vibe> all = vibeRepository.findAll();
        Map<String,String> byLabel = new HashMap<>(); // "맛있음" -> "맛있음"
        Map<String,String> byName  = new HashMap<>(); // "맛이 좋아요" -> "맛있음"
        Map<String,String> byCode  = new HashMap<>(); // "101" -> "맛있음" (Vibe.getCode() 존재 시)

        for (Vibe v : all) {
            if (v.getLabelCode() != null) {
                byLabel.put(v.getLabelCode().trim(), v.getLabelCode().trim());
            }
            if (v.getVibeName() != null && v.getLabelCode() != null) {
                byName.put(v.getVibeName().trim(), v.getLabelCode().trim());
            }
            // Vibe 엔티티에 code(정수)가 있다면 지원
            try {
                var codeField = v.getClass().getDeclaredField("code");
                codeField.setAccessible(true);
                Object codeVal = codeField.get(v);
                if (codeVal != null && v.getLabelCode() != null) {
                    byCode.put(String.valueOf(codeVal), v.getLabelCode().trim());
                }
            } catch (NoSuchFieldException | IllegalAccessException ignore) {
                // code 필드가 없거나 접근 불가하면 무시
            }
        }

        List<String> out = new ArrayList<>();
        for (String raw : keywords) {
            if (raw == null) continue;
            String s = raw.trim();
            if (s.isEmpty()) continue;

            // 숫자코드 → label_code (가능한 경우)
            if (s.matches("\\d+") && byCode.containsKey(s)) {
                out.add(byCode.get(s));
                continue;
            }
            // label_code 또는 vibe_name
            String label = byLabel.getOrDefault(s, byName.get(s));
            if (label != null) out.add(label);
        }
        return out;
    }
}