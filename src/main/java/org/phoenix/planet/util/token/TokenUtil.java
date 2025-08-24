package org.phoenix.planet.util.token;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtil {

    private static final SecureRandom secureRandom = new SecureRandom();

    // SecureRandom 기반 32바이트 랜덤 토큰 생성 (URL-safe Base64, padding 제거)
    public static String generateRandomToken() {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
