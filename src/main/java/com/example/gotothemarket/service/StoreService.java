package com.example.gotothemarket.service;

import com.example.gotothemarket.dto.HomeResponseDTO;
import com.example.gotothemarket.entity.*;
import com.example.gotothemarket.repository.*;
import com.example.gotothemarket.dto.StoreDTO;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;
    private final FavoriteRepository favoriteRepository;
    private final BadgeService badgeService;
    private final PhotoRepository photoRepository;
    private final MarketRepository marketRepository;
    private final StoreTypeRepository storeTypeRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final S3Service s3Service;
    private final BadgeRepository badgeRepository;

    @PersistenceContext
    private EntityManager em;

    // POST
    public StoreDTO.StoreResponseDTO createStore(StoreDTO.StoreRequestDTO dto) {

        // StoreType 조회
        StoreType storeType = storeTypeRepository.findById(dto.getStoreType())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상점 타입입니다. ID: " + dto.getStoreType()));

        //가장 가까운 시장 찾기
        Market nearestMarket = marketRepository.findNearestMarket(
                dto.getStoreCoord().getLat(),
                dto.getStoreCoord().getLng()
        );
        if (nearestMarket == null) {
            throw new RuntimeException("주변에 마켓을 찾을 수 없습니다.");
        }
        
        Point storeCoord = createPoint(
                dto.getStoreCoord() != null ? dto.getStoreCoord().getLat() : null,
                dto.getStoreCoord() != null ? dto.getStoreCoord().getLng() : null
        );

        Store store = Store.builder()
                .member(createTempMember())
                .market(nearestMarket) //가장 가까운 시장
                .storeType(storeType)
                .storeName(dto.getStoreName())
                .address(dto.getAddress())
                .storeCoord(storeCoord)
                .phoneNumber(dto.getPhoneNumber())
                .openingHours(dto.getOpeningHours())
                .closingHours(dto.getClosingHours())
                .favoriteCheck(false)
                .reviewCount(0)
                .build();

        Store savedStore = storeRepository.save(store);

        // 1) 가게 집계 테이블(분모/라벨별 분자) 보장
        initStoreStats(savedStore.getStoreId());

        // 2) 뱃지 자동 지급(가게 등록 관련 조건 평가)
        try {
            Integer memberId = (savedStore.getMember() != null) ? savedStore.getMember().getMemberId() : null;
            if (memberId != null) {
                badgeService.onStoreCreated(memberId);
            }
        } catch (Exception ignore) {
            // 배지 서비스 미연결/테스트 환경에서도 가게 생성은 진행
        }

        return createResponseDTO(savedStore);
    }

    // GET
    @Cacheable(value = "store-detail", key = "#storeId")
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
    @CacheEvict(value = {"store-detail", "home-data"}, allEntries = true)
    public StoreDTO.StoreDetailResponse updateStore(Integer storeId, StoreDTO.StoreUpdateDTO updateDTO) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("상점을 찾을 수 없습니다. ID: " + storeId));

        // StoreType 업데이트 처리
        StoreType storeType = store.getStoreType();
        if (updateDTO.getStoreType() != null) {
            storeType = storeTypeRepository.findById(updateDTO.getStoreType())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 상점 타입입니다. ID: " + updateDTO.getStoreType()));
        }
        // 아이콘
        String storeIconUrl = store.getStoreIcon();
        if (updateDTO.getStoreType() != null && !updateDTO.getStoreType().equals(store.getStoreType().getStoreTypeId())) {
            // storeType이 변경된 경우 새로운 아이콘 URL 설정
            storeIconUrl = s3Service.getStoreTypeIconUrl(storeType.getStoreTypeId());
        }

        Store.StoreBuilder builder = Store.builder()
                .storeId(store.getStoreId())
                .member(store.getMember())
                .market(store.getMarket())
                .storeType(storeType)
                .storeName(updateDTO.getStoreName() != null ? updateDTO.getStoreName() : store.getStoreName())
                .address(updateDTO.getAddress() != null ? updateDTO.getAddress() : store.getAddress())
                .phoneNumber(updateDTO.getPhoneNumber() != null ? updateDTO.getPhoneNumber() : store.getPhoneNumber())
                .openingHours(updateDTO.getOpeningHours() != null ? updateDTO.getOpeningHours() : store.getOpeningHours())
                .closingHours(updateDTO.getClosingHours() != null ? updateDTO.getClosingHours() : store.getClosingHours())
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

    //사진 업로드
    public StoreDTO.PhotoUploadResponse uploadStorePhoto(Integer storeId, MultipartFile file) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("가게를 찾을 수 없습니다. ID: " + storeId));
        // 유효성 검사
        validateImageFile(file);
        // S3에 파일 업로드
        String photoUrl = s3Service.uploadFile(file);

        Photo photo = Photo.builder()
                .store(store)
                .member(null)
                .photoUrl(photoUrl)
                .build();

        Photo savedPhoto = photoRepository.save(photo);

        return StoreDTO.PhotoUploadResponse.builder()
                .photoId(savedPhoto.getPhotoId())
                .photoUrl(savedPhoto.getPhotoUrl())
                .message("사진이 성공적으로 업로드되었습니다.")
                .uploadedAt(savedPhoto.getCreatedAt())
                .build();
    }

    // 두 좌표 간 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // 거리 (km)
    }
    // 가장 가까운 마켓 찾기
    private Market findNearestMarket(double storeLat, double storeLng) {
        List<Market> markets = marketRepository.findAll();

        Market nearestMarket = null;
        double minDistance = Double.MAX_VALUE;

        for (Market market : markets) {
            if (market.getMarketCoord() != null) {
                double marketLat = market.getMarketCoord().getY();
                double marketLng = market.getMarketCoord().getX();

                double distance = calculateDistance(storeLat, storeLng, marketLat, marketLng);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestMarket = market;
                }
            }
        }

        return nearestMarket;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("업로드할 파일이 없습니다.");
        }
        // 파일 크기 제한 (예: 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new RuntimeException("파일 크기는 10MB를 초과할 수 없습니다.");
        }
        // 이미지 파일 형식 검사
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("이미지 파일만 업로드 가능합니다.");
        }
    }

    public StoreDTO.PhotoDeleteResponse deleteStorePhoto(Integer storeId, Integer photoId) {
        // 가게 존재 여부 확인
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("가게를 찾을 수 없습니다. ID: " + storeId));

        // 사진 존재 여부 및 소속 확인
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("사진을 찾을 수 없습니다. ID: " + photoId));

        // 해당 사진이 해당 가게의 사진인지 확인
        if (!photo.getStore().getStoreId().equals(storeId)) {
            throw new RuntimeException("해당 가게의 사진이 아닙니다.");
        }

        // S3에서 파일 삭제
        try {
            s3Service.deleteFile(photo.getPhotoUrl());
        } catch (Exception e) {
            // S3 삭제 실패해도 DB는 삭제 (로그만 남기고 진행)
            System.err.println("S3 파일 삭제 실패: " + e.getMessage());
        }

        // DB에서 사진 삭제
        photoRepository.delete(photo);

        return StoreDTO.PhotoDeleteResponse.builder()
                .photoId(photoId)
                .message("사진이 성공적으로 삭제되었습니다.")
                .deletedAt(LocalDateTime.now())
                .build();
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
                .storeIcon(s3Service.getStoreTypeIconUrl(store.getStoreType().getStoreTypeId()))
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
    @Cacheable(value = "home-data", key = "#storeTypeId != null ? 'type-' + #storeTypeId : 'all-stores'")
    @Transactional(readOnly = true)
    public HomeResponseDTO getHomeData(Integer storeTypeId) {
        // Store 좌표 데이터 가져오기
        List<StoreRepository.StoreCoordProjection> storeProjections;
        if (storeTypeId != null) {
            storeProjections = storeRepository.findAllStoreCoordsWithStoreTypeFilter(storeTypeId);
        } else {
            storeProjections = storeRepository.findAllStoreCoords();
        }
        List<HomeResponseDTO.StoreCoordData> stores = storeProjections.stream()
                .map(projection -> HomeResponseDTO.StoreCoordData.builder()
                        .storeId(projection.getStoreId())
                        .latitude(projection.getLatitude())
                        .longitude(projection.getLongitude())
                        .storeTypeId(projection.getStoreTypeId())
                        .storeTypeName(projection.getStoreTypeName())
                        .build())
                .collect(Collectors.toList());

        // Market 좌표 데이터 가져오기
        List<MarketRepository.MarketCoordProjection> marketProjections = marketRepository.findAllMarketCoords();
        List<HomeResponseDTO.MarketCoordData> markets = marketProjections.stream()
                .map(projection -> HomeResponseDTO.MarketCoordData.builder()
                        .marketId(projection.getMarketId())
                        .latitude(projection.getLatitude())
                        .longitude(projection.getLongitude())
                        .build())
                .collect(Collectors.toList());

        return HomeResponseDTO.builder()
                .stores(stores)
                .markets(markets)
                .build();
    }

    // BadgeInfo 생성 (Member의 대표 뱃지 하나만 반환)
    // createBadgeInfo 메소드 수정
    private StoreDTO.BadgeInfo createBadgeInfo(Member member) {
        if (member == null || member.getAttachedBadgeId() == null) {
            return null;
        }

        // attached_badge_id로 직접 Badge 조회
        Optional<Badge> badgeOpt = badgeRepository.findById(member.getAttachedBadgeId());
        if (badgeOpt.isEmpty()) {
            return null;
        }

        Badge badge = badgeOpt.get();
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
                .storeIcon(s3Service.getStoreTypeIconUrl(savedStore.getStoreType().getStoreTypeId()))
                .message("상점이 성공적으로 등록되었습니다!")
                .build();
    }

    /**
     * 신규 가게에 대한 통계 보장:
     * - store_stat (분모) 1행 보장
     * - store_vibe_stat (가게 × 모든 라벨) 0으로 초기화된 행 보장
     */
    private void initStoreStats(Integer storeId) {
        if (storeId == null) return;

        // store_stat upsert
        em.createNativeQuery("""
            INSERT INTO store_stat (store_id, review_count, keyword_total, last_aggregated)
            VALUES (:sid, 0, 0.0, now())
            ON CONFLICT (store_id) DO NOTHING
        """)
          .setParameter("sid", storeId)
          .executeUpdate();

        // store_vibe_stat upsert (모든 라벨 0 초기화)
        em.createNativeQuery("""
            INSERT INTO store_vibe_stat (store_id, vibe_id, hit_count, ratio, updated_at)
            SELECT :sid, v.vibe_id, 0, 0.0, now()
            FROM vibe v
            ON CONFLICT (store_id, vibe_id) DO NOTHING
        """)
          .setParameter("sid", storeId)
          .executeUpdate();
    }
}