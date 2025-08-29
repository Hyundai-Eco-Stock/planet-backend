package org.phoenix.planet.service.payment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.PaymentError;
import org.phoenix.planet.error.payment.PaymentException;
import org.phoenix.planet.util.file.CloudFrontFileUtil;
import org.phoenix.planet.util.file.S3FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private final S3FileUtil s3FileUtil;
    private final CloudFrontFileUtil cloudFrontFileUtil;

    @Value("${qr.secret-key:default-qr-secret-key-2024}")
    private String qrSecretKey;

    @Value("${cloudfront.distribution-domain}")
    private String cloudFrontDomain;

    private static final int QR_CODE_SIZE = 300;
    private static final String QR_CODE_FORMAT = "PNG";

    /**
     * 픽업 주문용 QR 코드 생성 및 S3 업로드
     */
    @Override
    public String generatePickupQRCode(Long orderHistoryId) {
        try {
            log.info("픽업 QR 코드 생성 시작: orderHistoryId={}", orderHistoryId);

            // QR 코드 데이터 생성
            String qrData = generateQRData(orderHistoryId);

            // QR 코드 이미지 생성
            BufferedImage qrImage = generateQRCodeImage(qrData);

            // 이미지를 바이트 배열로 변환
            byte[] imageBytes = imageToBytes(qrImage);

            // S3에 업로드
            String s3Key = uploadQRCodeToS3(imageBytes, orderHistoryId);

            // CloudFront Signed URL 생성 (24시간 유효)
            String signedUrl = cloudFrontFileUtil.generateSignedUrl(s3Key, 24 * 60 * 60);

            log.info("픽업 QR 코드 생성 완료: orderHistoryId={}, url={}", orderHistoryId, signedUrl);
            return signedUrl;
        } catch (Exception e) {
            log.error("QR 코드 생성 중 오류 발생: orderHistoryId={}", orderHistoryId, e);
            throw new PaymentException(PaymentError.QR_CODE_GENERATION_FAILED);
        }
    }

    /**
     * QR 코드 검증
     */
    @Override
    public boolean validateQRCode(String qrCodeData) {
        try {
            if (qrCodeData == null || qrCodeData.trim().isEmpty()) {
                return false;
            }

            // Base64 디코딩
            String decodedData = new String(Base64.getDecoder().decode(qrCodeData));

            // 데이터 파싱 (ORDER:123:TIME:20241225123000:HASH:abcd1234)
            String[] parts = decodedData.split(":");
            if (parts.length != 6) {
                return false;
            }

            // 해시 검증
            String originalData = String.format("%s:%s:%s:%s", parts[0], parts[1], parts[2], parts[3]);
            String expectedHash = generateSecureHash(originalData);

            return expectedHash.equals(parts[5]);

        } catch (Exception e) {
            log.error("QR 코드 검증 중 오류 발생", e);
            return false;
        }
    }

    /**
     * QR 코드 데이터 생성
     */
    private String generateQRData(Long orderHistoryId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String plainData = String.format("ORDER:%d:TIME:%s", orderHistoryId, timestamp);
        String secureHash = generateSecureHash(plainData);
        String qrData = String.format("%s:HASH:%s", plainData, secureHash);

        return Base64.getEncoder().encodeToString(qrData.getBytes());
    }

    /**
     * QR 코드 이미지 생성
     */
    private BufferedImage generateQRCodeImage(String qrData) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 이미지를 바이트 배열로 변환
     */
    private byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, QR_CODE_FORMAT, baos);
        return baos.toByteArray();
    }

    /**
     * 기존 S3FileUtil을 사용해서 QR 코드 업로드
     */
    private String uploadQRCodeToS3(byte[] imageBytes, Long orderHistoryId) {
        try {
            String qrPath = "qr-codes/" + orderHistoryId + "/";
            String filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_qr.png";
            String fullFilePath = qrPath + filename;

            // S3FileUtil의 uploadBytes 메서드가 있다면 사용
            // 없다면 S3FileUtil에 추가 메서드 구현 필요
            s3FileUtil.uploadBytes(fullFilePath, imageBytes, "image/png");

            return fullFilePath;
        } catch (Exception e) {
            log.error("QR 코드 S3 업로드 실패: orderHistoryId={}", orderHistoryId, e);
            throw new PaymentException(PaymentError.QR_CODE_UPLOAD_FAILED);
        }
    }

    /**
     * 보안 해시 생성 (QR 코드 위변조 방지)
     */
    private String generateSecureHash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedData = data + qrSecretKey;
            byte[] hashBytes = md.digest(saltedData.getBytes());

            // 해시를 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.substring(0, 16); // 처음 16자리만 사용

        } catch (NoSuchAlgorithmException e) {
            log.error("해시 알고리즘을 찾을 수 없습니다.", e);
            throw new PaymentException(PaymentError.QR_CODE_GENERATION_FAILED);
        }
    }

}