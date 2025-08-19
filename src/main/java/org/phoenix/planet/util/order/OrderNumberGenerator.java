package org.phoenix.planet.util.order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 주문번호 생성
 * ORD + YYYYMMDD + HHMMSS + 랜덤 4자리
 */
public class OrderNumberGenerator {

    private static final String PREFIX = "ORD";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");
    private static final Random RANDOM = new Random();

    public static String generate() {
        LocalDateTime now = LocalDateTime.now();

        String datePart = now.format(DATE_FORMAT);
        String timePart = now.format(TIME_FORMAT);
        String randomPart = String.format("%04d", RANDOM.nextInt(10000));

        return PREFIX + datePart + timePart + randomPart;
    }

}
