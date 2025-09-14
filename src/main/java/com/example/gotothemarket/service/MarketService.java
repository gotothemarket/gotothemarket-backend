package com.example.gotothemarket.service;

import com.example.gotothemarket.dto.MarketDetailResponse;
import com.example.gotothemarket.dto.MarketDto;
import com.example.gotothemarket.entity.Market;
import com.example.gotothemarket.repository.MarketRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MarketService {

    private final MarketRepository marketRepository;
    private final S3Service s3Service;

    public MarketDto findNearestMarket(double lat, double lng) {
        Market market = marketRepository.findNearestMarket(lat, lng);
        return new MarketDto(market.getMarketId(), market.getMarketName());
    }
    
    // S3 이미지 url db에 저장
    @CacheEvict(value = "market-detail", key = "#marketId")
    @Transactional
    public void loadAndSaveMarketImages(Integer marketId) {
        // Market 엔티티를 조회
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new RuntimeException("시장을 찾을 수 없습니다. ID: " + marketId));

        // S3에서 이미지 URL들 가져오기
        List<String> mainImageUrls = s3Service.getMarketMainImageUrls(marketId);
        List<String> eventImageUrls = s3Service.getMarketEventImageUrls(marketId);

        // 기존 이미지 URL들 클리어하고 새로 설정
        market.getMarketMainImageUrls().clear();
        market.getMarketMainImageUrls().addAll(mainImageUrls);

        market.getMarketEventImageUrls().clear();
        market.getMarketEventImageUrls().addAll(eventImageUrls);

        // 엔티티 저장 (더티 체킹으로 자동 업데이트)
        marketRepository.save(market);
    }

    // 시장 이미지 url 로드하기
    public void loadAllMarketImages() {
        List<Market> markets = marketRepository.findAll();
        for (Market market : markets) {
            loadAndSaveMarketImages(market.getMarketId());
        }
    }

    @Cacheable(value = "market-detail", key = "#marketId")
    public MarketDetailResponse getMarketDetail(Integer marketId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new RuntimeException("시장을 찾을 수 없습니다. ID: " + marketId));

        // 이미지 URL들이 비어있다면 S3에서 로드
        if (market.getMarketMainImageUrls().isEmpty() || market.getMarketEventImageUrls().isEmpty()) {
            loadAndSaveMarketImages(marketId);
            // 다시 조회해서 업데이트된 이미지 URL들 가져오기
            market = marketRepository.findById(marketId).get();
        }

        return convertToDetailResponse(market);
    }

    //Convert하기
    private MarketDetailResponse convertToDetailResponse(Market market) {
        MarketDetailResponse.MarketDetailResponseBuilder builder = MarketDetailResponse.builder()
                .marketId(market.getMarketId())
                .marketName(market.getMarketName())
                .marketAddress(market.getMarketAddress())
                .openingYears(market.getOpeningYears())
                .openingCycle(market.getOpeningCycle())
                .storeCount(market.getStoreCount())
                .transport(market.getTransport())
                .parking(market.getParking())
                .toilet(market.getToilet())
                .marketMainImageUrls(market.getMarketMainImageUrls())
                .marketEventImageUrls(market.getMarketEventImageUrls());

        // Point 좌표를 위도/경도로 변환
        if (market.getMarketCoord() != null) {
            builder.latitude(market.getMarketCoord().getY())
                    .longitude(market.getMarketCoord().getX());
        }

        return builder.build();
    }

}