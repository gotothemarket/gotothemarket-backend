package com.example.gotothemarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        try {
            // 고유한 파일명 생성
            String fileName = generateFileName(file.getOriginalFilename());

            // S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 업로드된 파일의 URL 반환
            String fileUrl = String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s",
                    bucketName, fileName);

            log.info("파일 업로드 성공: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "store-images/" + UUID.randomUUID().toString() + extension;
    }
    
    // 시장 대표 사진
    public List<String> getMarketMainImageUrls(Integer marketId) {
        List<String> imageUrls = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String fileName = String.format("market-images/main/market_%d_main_%d.jpg", marketId, i);
            String imageUrl = String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s",
                    bucketName, fileName);
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    // 시장 행사 사진
    public List<String> getMarketEventImageUrls(Integer marketId) {
        List<String> imageUrls = new ArrayList<>();
        String fileName = String.format("market-images/event/market_%d_event_1.jpg", marketId);
        String imageUrl = String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s",
                bucketName, fileName);
        imageUrls.add(imageUrl);
        return imageUrls;
    }

    // 가게 종류 아이콘
    public String getStoreTypeIconUrl(Integer storeTypeId) {
        if (storeTypeId == null || storeTypeId < 1 || storeTypeId > 8) {
            return null;
        }
        String fileName = String.format("store-type-icons/store_type_%d_icon.png", storeTypeId);
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucketName, fileName);
    }
    
}