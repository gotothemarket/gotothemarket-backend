package com.example.gotothemarket.service;

import com.example.gotothemarket.entity.*;
import com.example.gotothemarket.repository.StoreStatRepository;
import com.example.gotothemarket.repository.StoreVibeStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VibeAggregationService {
    private final StoreStatRepository storeStatRepo;
    private final StoreVibeStatRepository storeVibeStatRepo;
    private static final double ALPHA = 1.0; // Laplace smoothing
    private static final int LABEL_CARDINALITY = 24; // 활성 라벨 수

    @Transactional
    public void applyReviewCreated(Store store, Map<Vibe, Double> picked) {
        // 1) StoreStat upsert
        StoreStat stat = storeStatRepo.findById(store.getStoreId())
                .orElse(StoreStat.builder()
                        .store(store).storeId(store.getStoreId())
                        .reviewCount(0).keywordTotal(0).lastAggregated(LocalDateTime.now())
                        .build());
        stat.incReviews(1);
        double addToTotal = picked.values().stream().mapToDouble(d->d).sum(); // confidence 합(=가중치). 정수 카운트면 picked.size()
        stat.incKeywordTotal(addToTotal);
        stat.touch();
        storeStatRepo.save(stat);

        // 2) StoreVibeStat 증분
        picked.forEach((vibe, weight) -> {
            StoreVibeKey key = new StoreVibeKey(store.getStoreId(), vibe.getVibeId());
            StoreVibeStat s = storeVibeStatRepo.findById(key)
                    .orElse(StoreVibeStat.builder()
                            .id(key).store(store).vibe(vibe)
                            .hitCount(0).ratio(0).updatedAt(LocalDateTime.now())
                            .build());
            s.addHit(weight);  // weight=1 또는 confidence
            s.touch();
            storeVibeStatRepo.save(s);
        });

        // 3) 비율 재계산(해당 가게만)
        recalcRatiosForStore(store.getStoreId());
    }

    @Transactional
    public void applyReviewUpdated(Store store, Map<Vibe, Double> oldPicked, Map<Vibe, Double> newPicked) {
        // 가중치 변화까지 반영: 공통 라벨은 (new-old) 만큼 증감, 누락은 음수, 신규는 양수
        StoreStat stat = storeStatRepo.findById(store.getStoreId()).orElseThrow();
        double deltaTotal = 0.0;

        // 1) 공통 및 신규 라벨 처리
        for (Map.Entry<Vibe, Double> e : newPicked.entrySet()) {
            Vibe vibe = e.getKey();
            double newW = e.getValue() == null ? 0.0 : e.getValue();
            double oldW = oldPicked.getOrDefault(vibe, 0.0);
            double delta = newW - oldW; // 동일 라벨의 가중치 변화
            if (delta != 0.0) {
                adjHit(store, vibe, delta);
                deltaTotal += delta;
            }
        }

        // 2) 삭제된 라벨 처리 (new에 없고 old에만 있는 것)
        for (Map.Entry<Vibe, Double> e : oldPicked.entrySet()) {
            Vibe vibe = e.getKey();
            if (newPicked.containsKey(vibe)) continue; // 이미 위에서 처리
            double oldW = e.getValue() == null ? 0.0 : e.getValue();
            if (oldW != 0.0) {
                adjHit(store, vibe, -oldW);
                deltaTotal -= oldW;
            }
        }

        // 3) 분모(keyword_total) 조정 및 저장
        if (deltaTotal != 0.0) {
            stat.incKeywordTotal(deltaTotal);
        }
        stat.touch();
        storeStatRepo.save(stat);

        // 4) 비율 재계산
        recalcRatiosForStore(store.getStoreId());
    }

    @Transactional
    public void applyReviewDeleted(Store store, Map<Vibe, Double> picked) {
        StoreStat stat = storeStatRepo.findById(store.getStoreId()).orElseThrow();
        stat.incReviews(-1);
        double dec = picked.values().stream().mapToDouble(d->d).sum();
        stat.incKeywordTotal(-dec);
        stat.touch();
        storeStatRepo.save(stat);

        picked.forEach((vibe, w) -> adjHit(store, vibe, -w));
        recalcRatiosForStore(store.getStoreId());
    }

    private void adjHit(Store store, Vibe vibe, double delta) {
        if (delta == 0.0) return;
        StoreVibeKey key = new StoreVibeKey(store.getStoreId(), vibe.getVibeId());
        StoreVibeStat s = storeVibeStatRepo.findById(key)
                .orElse(StoreVibeStat.builder()
                        .id(key).store(store).vibe(vibe)
                        .hitCount(0).ratio(0).updatedAt(LocalDateTime.now())
                        .build());
        s.addHit(delta);
        if (s.getHitCount() < 0) {
            // 음수 방지: 0으로 클램프
            s.addHit(-s.getHitCount());
        }
        s.touch();
        storeVibeStatRepo.save(s);
    }

    private void recalcRatiosForStore(Integer storeId) {
        StoreStat stat = storeStatRepo.findById(storeId).orElseThrow();
        double denom = stat.getKeywordTotal();
        var rows = storeVibeStatRepo.findByStoreStoreIdOrderByRatioDesc(storeId);
        int V = LABEL_CARDINALITY; // 또는 rows.size() / VibeRepository.countActive()
        double denomAdj = denom + ALPHA * V;
        for (StoreVibeStat row : rows) {
            double numer = row.getHitCount() + ALPHA;
            double r = (denomAdj > 0.0) ? (numer / denomAdj) : 0.0;
            row.setRatio(r);
            row.touch();
        }
        storeVibeStatRepo.saveAll(rows);
    }
}