package com.example.gotothemarket.service;

import com.example.gotothemarket.entity.Market;
import com.example.gotothemarket.entity.Member;
import com.example.gotothemarket.entity.Store;
import com.example.gotothemarket.entity.StoreType;
import com.example.gotothemarket.repository.StoreRepository;
import com.example.gotothemarket.dto.StoreDTO;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public StoreDTO.StoreResponseDTO createStore(StoreDTO.StoreRequestDTO dto) {
        // Point 좌표 생성
        Point storeCoord = createPoint(dto.getLatitude(), dto.getLongitude());

        // Store 엔티티 생성
        Store store = Store.builder()
                .member(createTempMember())
                .market(createTempMarket())
                .storeType(createTempStoreType())
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

        // 저장
        Store savedStore = storeRepository.save(store);

        // 응답 DTO 생성
        return createResponseDTO(savedStore, dto);
    }

    private Point createPoint(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    private Member createTempMember() {
        return Member.builder().memberId(1).build();
    }

    private Market createTempMarket() {
        return Market.builder().marketId(1).build();
    }

    private StoreType createTempStoreType() {
        return StoreType.builder().storeTypeId(1).build();
    }

    private LocalTime createTime(Integer hour, Integer minute) {
        if (hour == null || minute == null) {
            return null;
        }
        return LocalTime.of(hour, minute);
    }

    private StoreDTO.StoreResponseDTO createResponseDTO(Store savedStore, StoreDTO.StoreRequestDTO dto) {
        Double latitude = null;
        Double longitude = null;

        if (savedStore.getStoreCoord() != null) {
            latitude = savedStore.getStoreCoord().getY();
            longitude = savedStore.getStoreCoord().getX();
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
    }
}