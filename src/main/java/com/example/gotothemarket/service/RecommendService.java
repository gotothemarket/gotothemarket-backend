package com.example.gotothemarket.service;

import com.example.gotothemarket.dto.CourseRequest;
import com.example.gotothemarket.dto.CourseResponse;
import com.example.gotothemarket.dto.TopStoreDto;
import com.example.gotothemarket.entity.Vibe;
import com.example.gotothemarket.repository.RecommendRepository;
import com.example.gotothemarket.repository.VibeRepository;
import com.example.gotothemarket.repository.StoreRepository;
import com.example.gotothemarket.repository.MarketRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Set;
import java.util.HashSet;

@Service
public class RecommendService {

    private final RecommendRepository recommendRepository;
    private final VibeRepository vibeRepository;
    private final StoreRepository storeRepository;
    private final MarketRepository marketRepository;

    public RecommendService(RecommendRepository recommendRepository,
                            VibeRepository vibeRepository,
                            StoreRepository storeRepository,
                            MarketRepository marketRepository) {
        this.recommendRepository = recommendRepository;
        this.vibeRepository = vibeRepository;
        this.storeRepository = storeRepository;
        this.marketRepository = marketRepository;
    }

    public CourseResponse recommendCourses(CourseRequest req) {
        // 1) 시장 정문 좌표(Point) 조회: X=lng, Y=lat
        Point entrance = marketRepository.findEntranceCoord(req.getMarketId());
        if (entrance == null) {
            throw new IllegalArgumentException("시장 정보를 찾을 수 없습니다: " + req.getMarketId());
        }
        double refLat = entrance.getY();
        double refLng = entrance.getX();

        List<CourseResponse.Course> picked = new ArrayList<>();
        Set<Integer> seenStoreIds = new HashSet<>();

        // 2) 업종 세트별 후보 가게 1곳 선별
        for (int i = 0; i < req.getSets().size(); i++) {
            var set = req.getSets().get(i);

            // 키워드 → label_code 매핑
            List<String> labels = toLabelCodes(set.getKeywords());
            if (labels.isEmpty()) continue; // 매칭 실패 시 스킵(필요 시 400 처리)

            TopStoreDto top = recommendRepository
                    .pickTopStoreByLabels(req.getMarketId(), set.getStoreType(), labels);
            if (top == null) continue;

            int sid = top.getStoreId();
            if (seenStoreIds.contains(sid)) {
                continue; // 이미 추천된 가게는 스킵
            }

            // 3) 가게 좌표 조회 (Store.storeCoord: Point)
            var locOpt = storeRepository.findLocById(top.getStoreId());
            if (locOpt.isEmpty()) continue;
            var loc = locOpt.get();
            Point p = loc.getStoreCoord();
            if (p == null) continue;

            double lat = p.getY();
            double lng = p.getX();
            long distance = Math.round(haversineMeters(refLat, refLng, lat, lng));

            // ★ 응답 Course에 좌표/거리 포함 (coord=GeoJSON Point, distance_m 포함)
            picked.add(new CourseResponse.Course(
                    0, // 정렬 후 재부여
                    top.getStoreId(),
                    top.getStoreName(),
                    top.getStoreType(),
                    set.getKeywords(),
                    new CourseResponse.GeoPoint("Point", new double[]{lng, lat}),
                    distance
            ));
            seenStoreIds.add(sid);
        }

        // 4) 거리 오름차순 정렬 + order 재부여(1부터)
        picked.sort(Comparator.comparingLong(CourseResponse.Course::distanceM));
        List<CourseResponse.Course> ordered = new ArrayList<>();
        for (int i = 0; i < picked.size(); i++) {
            var c = picked.get(i);
            ordered.add(new CourseResponse.Course(
                    i + 1,
                    c.storeId(),
                    c.storeName(),
                    c.storeType(),
                    c.keywords(),
                    c.coord(),
                    c.distanceM()
            ));
        }

        return new CourseResponse(true, 200, new CourseResponse.Data(ordered));
    }

    private static double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371000.0; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
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