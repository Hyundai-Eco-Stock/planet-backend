package org.phoenix.planet.util.file;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.configuration.file.CloudFrontProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloudFrontFileUtil {

    private final CloudFrontProperties properties;

    /// 지정된 경로로 Signed URL 생성
    public String generateSignedUrl(String objectKey, long validSeconds) {

        Date expirationDate = new Date(System.currentTimeMillis() + validSeconds * 1000);

        try {
            String encodedObjectKey = URLEncoder.encode(objectKey, StandardCharsets.UTF_8)
                .replace("+", "%20"); // S3 URL에서 + 를 %20 으로 교체 (공백 문제 방지)

            return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                SignerUtils.Protocol.https,
                properties.getDistributionDomain(),
                new File(properties.getPrivateKeyPath()),
                encodedObjectKey,
                properties.getKeyPairId(),
                expirationDate
            );
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}