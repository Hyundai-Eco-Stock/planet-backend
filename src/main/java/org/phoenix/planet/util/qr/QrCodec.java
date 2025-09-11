package org.phoenix.planet.util.qr;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class QrCodec {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static class Parsed {
        public final long orderId;
        public final LocalDateTime time;
        public Parsed(long orderId, LocalDateTime time){ this.orderId = orderId; this.time = time; }
    }

    /** d(Base64URL) 디코드하고 HMAC/TTL 검증 후 orderId, time 반환 */
    public static Parsed parseAndVerify(String d, String secret, long allowedSkewSeconds) {
        String payload = new String(Base64.getUrlDecoder().decode(d));
        // v1|ORDER_ID|{id}|TIME|{ts}|SIG|{sig}
        String[] p = payload.split("\\|");
        if (p.length != 7 || !"v1".equals(p[0]) || !"ORDER_ID".equals(p[1]) || !"TIME".equals(p[3]) || !"SIG".equals(p[5])) {
            throw new IllegalArgumentException("QR format invalid");
        }
        long orderId = Long.parseLong(p[2]);
        String ts = p[4];
        String sig = p[6];

        String plain = String.format("v1|ORDER_ID|%d|TIME|%s", orderId, ts);
        String expect = hmacSha256Base64Url(plain, secret);
        // 상수시간 비교
        if (!java.security.MessageDigest.isEqual(expect.getBytes(), sig.getBytes())) {
            throw new SecurityException("QR signature invalid");
        }
        LocalDateTime t = LocalDateTime.parse(ts, FMT);
        long diff = Math.abs(Duration.between(t, LocalDateTime.now()).getSeconds());
        if (diff > allowedSkewSeconds) {
            throw new IllegalStateException("QR expired");
        }
        return new Parsed(orderId, t);
    }

    public static String hmacSha256Base64Url(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes()));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

}
