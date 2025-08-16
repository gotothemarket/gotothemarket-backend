package com.example.gotothemarket.service;

import com.example.gotothemarket.entity.*;
import com.example.gotothemarket.repository.StoreRepository;
import com.example.gotothemarket.repository.FavoriteRepository;
import com.example.gotothemarket.dto.StoreDTO;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;
    private final FavoriteRepository favoriteRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    // POST
    public StoreDTO.StoreResponseDTO createStore(StoreDTO.StoreRequestDTO dto) {
        Point storeCoord = createPoint(
                dto.getStoreCoord() != null ? dto.getStoreCoord().getLat() : null,
                dto.getStoreCoord() != null ? dto.getStoreCoord().getLng() : null
        );

        Store store = Store.builder()
                .member(createTempMember())
                .market(createTempMarket())
                .storeType(createTempStoreType())
                .storeName(dto.getStoreName())
                .address(dto.getAddress())
                .storeCoord(storeCoord)
                .phoneNumber(dto.getPhoneNumber())
                .openingHours(dto.getOpeningHours())
                .closingHours(dto.getClosingHours())
                .storeIcon(dto.getStoreIcon())
                .favoriteCheck(false)
                .reviewCount(0)
                .build();

        Store savedStore = storeRepository.save(store);
        return createResponseDTO(savedStore);
    }

    // GET
    @Transactional(readOnly = true)
    public StoreDTO.StoreDetailResponse getStoreDetail(Integer storeId) {
        Store store = storeRepository.findStoreWithBasicDetailsById(storeId)
                .orElseThrow(() -> new RuntimeException("상점을 찾을 수 없습니다. ID: " + storeId));

        return StoreDTO.StoreDetailResponse.builder()
                .store(createStoreInfo(store))
                .photos(createPhotoInfoList(store.getPhotos()))
                .reviewSummary(createReviewSummary(store))
                .reviews(createReviewInfoList(store.getReviews()))
                .build();
    }

    // PATCH
    public StoreDTO.StoreDetailResponse updateStore(Integer storeId, StoreDTO.StoreUpdateDTO updateDTO) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("상점을 찾을 수 없습니다. ID: " + storeId));

        Store.StoreBuilder builder = Store.builder()
                .storeId(store.getStoreId())
                .member(store.getMember())
                .market(store.getMarket())
                .storeType(store.getStoreType())
                .storeName(updateDTO.getStoreName() != null ? updateDTO.getStoreName() : store.getStoreName())
                .address(updateDTO.getAddress() != null ? updateDTO.getAddress() : store.getAddress())
                .phoneNumber(updateDTO.getPhoneNumber() != null ? updateDTO.getPhoneNumber() : store.getPhoneNumber())
                .openingHours(updateDTO.getOpeningHours() != null ? updateDTO.getOpeningHours() : store.getOpeningHours())
                .closingHours(updateDTO.getClosingHours() != null ? updateDTO.getClosingHours() : store.getClosingHours())
                .storeIcon(updateDTO.getStoreIcon() != null ? updateDTO.getStoreIcon() : store.getStoreIcon())
                .averageRating(store.getAverageRating())
                .reviewCount(store.getReviewCount())
                .favoriteCheck(store.getFavoriteCheck());

        // 위도 경도 point로 바꾸기
        if (updateDTO.getStoreCoord() != null &&
                updateDTO.getStoreCoord().getLat() != null &&
                updateDTO.getStoreCoord().getLng() != null) {
            Point newCoord = createPoint(updateDTO.getStoreCoord().getLat(), updateDTO.getStoreCoord().getLng());
            builder.storeCoord(newCoord);
        } else {
            builder.storeCoord(store.getStoreCoord());
        }

        Store updatedStore = storeRepository.save(builder.build());

        // 업데이트 후 상세 정보 다시 조회
        return getStoreDetail(updatedStore.getStoreId());
    }

    // StoreInfo 생성
    private StoreDTO.StoreInfo createStoreInfo(Store store) {
        StoreDTO.StoreCoord coord = null;
        if (store.getStoreCoord() != null) {
            coord = StoreDTO.StoreCoord.builder()
                    .lat(store.getStoreCoord().getY())
                    .lng(store.getStoreCoord().getX())
                    .build();
        }
        // 멤버 고정시키기
        Member member = Member.builder().memberId(1).build();
        boolean isFavorite = favoriteRepository.existsByMemberAndStore(member, store);


        return StoreDTO.StoreInfo.builder()
                .storeId(store.getStoreId())
                .memberId(store.getMember() != null ? store.getMember().getMemberId() : null)
                .marketId(store.getMarket() != null ? store.getMarket().getMarketId() : null)
                .storeType(store.getStoreType() != null ? store.getStoreType().getStoreTypeId() : null)
                .typeName(store.getStoreType() != null ? store.getStoreType().getTypeName() : null)
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .storeCoord(coord)
                .phoneNumber(store.getPhoneNumber())
                .openingHours(store.getOpeningHours())
                .closingHours(store.getClosingHours())
                .storeIcon(store.getStoreIcon())
                .favoriteCheck(isFavorite)
                .build();
    }

    // PhotoInfo 리스트 생성
    private List<StoreDTO.PhotoInfo> createPhotoInfoList(List<Photo> photos) {
        return photos.stream()
                .map(photo -> StoreDTO.PhotoInfo.builder()
                        .photoId(photo.getPhotoId())
                        .photoUrl(photo.getPhotoUrl())
                        .createdAt(photo.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ReviewSummary 생성
    private StoreDTO.ReviewSummary createReviewSummary(Store store) {
        return StoreDTO.ReviewSummary.builder()
                .averageRating(store.getAverageRating())
                .reviewCount(store.getReviewCount())
                .build();
    }

    // ReviewInfo 리스트 생성
    private List<StoreDTO.ReviewInfo> createReviewInfoList(List<Review> reviews) {
        return reviews.stream()
                .map(review -> StoreDTO.ReviewInfo.builder()
                        .reviewId(review.getReviewId())
                        .memberId(review.getMember() != null ? review.getMember().getMemberId() : null)
                        .memberNickname(review.getMember() != null ? review.getMember().getNickname() : null)
                        .badge(createBadgeInfo(review.getMember()))
                        .rating(review.getRating())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // BadgeInfo 생성 (Member의 대표 뱃지 하나만 반환)
    private StoreDTO.BadgeInfo createBadgeInfo(Member member) {
        if (member == null || member.getBadges() == null || member.getBadges().isEmpty()) {
            return null;
        }

        // 지금은 대표 뱃지로 첫 번째 배지를 반환 TODO: 대표뱃지 구현 후 대표뱃지로 하기
        Badge badge = member.getBadges().get(0);

        return StoreDTO.BadgeInfo.builder()
                .badgeId(badge.getBadgeId())
                .badgeName(badge.getBadgeName())
                .badgeIcon(badge.getBadgeIcon())
                .build();
    }

    // 위도 경도 point로 다시 바꾸기
    private Point createPoint(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    //TODO: 멤버, 마켓, 점포 유형 만들면 그거쓰고 지우기
    private Member createTempMember() {
        return Member.builder().memberId(1).build();
    }

    private Market createTempMarket() {
        return Market.builder().marketId(1).build();
    }

    private StoreType createTempStoreType() {
        return StoreType.builder().storeTypeId(1).build();
    }

    private StoreDTO.StoreResponseDTO createResponseDTO(Store savedStore) {
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
                .openingTime(savedStore.getOpeningHours())
                .closingTime(savedStore.getClosingHours())
                .storeIcon(savedStore.getStoreIcon())
                .message("상점이 성공적으로 등록되었습니다!")
                .build();
    }
}