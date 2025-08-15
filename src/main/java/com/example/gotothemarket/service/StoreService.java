package com.example.gotothemarket.service;

import com.example.gotothemarket.entity.Market;
import com.example.gotothemarket.entity.Member;
import com.example.gotothemarket.entity.Store;
import com.example.gotothemarket.entity.StoreType;
import com.example.gotothemarket.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import com.example.gotothemarket.dto.StoreDTO;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreDTO.StoreResponseDTO createStore(StoreDTO.StoreRequestDTO dto) {
        try {
            // Point 타입 생성
            Point storeCoord = null;
            if (dto.getLatitude() != null && dto.getLongitude() != null) {
                GeometryFactory geometryFactory = new GeometryFactory();
                storeCoord = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
            }

            Store store = Store.builder()
                    .member(Member.builder().memberId(1).build())  // 임시 객체
                    .market(Market.builder().marketId(1).build())  // 임시 객체
                    .storeType(StoreType.builder().storeTypeId(1).build())  // 임시 객체
                    .storeName(dto.getStoreName())
                    .address(dto.getAddress())
                    .storeCoord(storeCoord)
                    .phoneNumber(dto.getPhoneNumber())
                    .openingHours(createTime(dto.getOpenHour(), dto.getOpenMinute()))
                    .closingHours(createTime(dto.getCloseHour(), dto.getCloseMinute()))
                    .storeIcon(dto.getStoreIcon())
                    .favoriteCheck(false)
                    .reviewCount(0)
                    .build();

            Store savedStore = storeRepository.save(store);

            // 응답시 PGpoint에서 위도/경도 추출
            Double latitude = null;
            Double longitude = null;
            if (savedStore.getStoreCoord() != null) {
                latitude = savedStore.getStoreCoord().getY();   // 위도
                longitude = savedStore.getStoreCoord().getX();  // 경도
            }

            return StoreDTO.StoreResponseDTO.builder()
                    .storeId(savedStore.getStoreId())
                    .storeName(savedStore.getStoreName())
                    .address(savedStore.getAddress())
                    .latitude(latitude)
                    .longitude(longitude)
                    .phoneNumber(savedStore.getPhoneNumber())
                    .openHour(dto.getOpenHour())
                    .openMinute(dto.getOpenMinute())
                    .closeHour(dto.getCloseHour())
                    .closeMinute(dto.getCloseMinute())
                    .storeIcon(savedStore.getStoreIcon())
                    .message("상점이 성공적으로 등록되었습니다!")
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("상점 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private LocalTime createTime(Integer hour, Integer minute) {
        if (hour == null || minute == null) return null;
        return LocalTime.of(hour, minute);
    }
}