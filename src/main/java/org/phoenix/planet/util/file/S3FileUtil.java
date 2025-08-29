package org.phoenix.planet.util.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


/**
 * 업로드만!!
 */
@Component
@RequiredArgsConstructor
public class S3FileUtil {

    private final S3Client s3Client;

    @Value("${s3.upload-bucket.name}")
    private String BUCKET_NAME;

    @Value("${s3.upload-path.member-profile}")
    private String MEMBER_PROFILE_IMG_PATH;

    @Value("${s3.upload-path.product-img}")
    private String PRODUCT_IMG_PATH;


    /**
     * 멤버 프로필 이미지 단일 업로드
     *
     * @param file
     * @param memberId
     * @return 저장된 파일 경로 (db에 저장할 것)
     */
    public String uploadMemberProfile(MultipartFile file, long memberId) {

        return upload(file, MEMBER_PROFILE_IMG_PATH + memberId + "/");
    }

    /**
     * 상품 이미지 파일 목록 다중 업로드
     *
     * @param file
     * @param productId
     * @return 저장된 파일 경로 (db에 저장할 것)
     */
    public String uploadProductImageFile(MultipartFile file, long productId) {

        return upload(file, PRODUCT_IMG_PATH + productId + "/");
    }

    /**
     * S3 버킷에 업로드
     *
     * @param file
     * @param path : path/to/ 형식
     * @return 저장 파일 경로
     * @throws Exception
     */
    private String upload(MultipartFile file, String path) {

        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String fullFilePath = path + filename;

            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(fullFilePath)
                    .contentType(file.getContentType())
                    .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            return fullFilePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 다중 파일 업로드
     *
     * @param fileList
     * @param path
     * @return
     */
    private List<String> uploadFiles(List<MultipartFile> fileList, String path) {

        List<String> filenameList = new ArrayList<>();

        fileList.forEach(file -> {
            String filePath = upload(file, path);
            filenameList.add(filePath);
        });
        return filenameList;
    }

    /**
     * 다중 파일 삭제
     *
     * @param filePathList: 파일 경로 목록
     */
    public void removeFiles(List<String> filePathList) {

        filePathList.forEach(filePath ->
            s3Client.deleteObject(builder -> builder
                .bucket(BUCKET_NAME)
                .key(filePath)
                .build()
            )
        );
    }

    // 파일 확장자 유효성 검사 - 파일 위치 확인 필요
    public void validateImgFileType(String filePath) {

        if (filePath == null) {
            throw new IllegalArgumentException("파일명이 존재하지 않습니다.");
        }
        String lower = filePath.toLowerCase();
        if (!(lower.endsWith(".jpeg") || lower.endsWith(".jpg") || lower.endsWith(".png")
            || lower.endsWith(".pdf"))) {
            throw new IllegalArgumentException("허용되지 않은 파일 확장자입니다. (jpg, png, pdf만 가능)");
        }
    }

    /**
     * QR 코드 바이트 배열을 S3에 업로드
     */
    public String uploadBytes(String filePath, byte[] imageBytes, String contentType) {
        try {
            // ByteArrayInputStream으로 변환하여 업로드
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(filePath)
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, imageBytes.length));
            }

            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("S3 업로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

}