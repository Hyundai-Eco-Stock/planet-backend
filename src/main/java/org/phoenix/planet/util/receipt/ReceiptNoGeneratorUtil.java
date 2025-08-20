package org.phoenix.planet.util.receipt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class ReceiptNoGeneratorUtil {

    public String generate(
        long brandId,
        int posId,
        long dailySeq,
        LocalDateTime now) {

        // 1. now = 현재 시간(서버)
        //	 • date = now.format(YYMMDD) → 6자리
        //	 • time = now.format(HHmm) → 4자리
        // 2. storeId = 점포 ID 3자리로 zero-pad (예: 10 → 010) = brandId
        // 3. posId = POS ID 2자리로 zero-pad (예: 69 → 69)
        // 4. seq = 해당 POS 의 “당일 시퀀스” 6자리 zero-pad
        //	 • 매일 00:00에 리셋 (트랜잭션/레디스 카운터 사용)
        // 5. base = date + time + storeId + posId + seq  (21자리)
        // 6. check = Luhn(base) (한 자리)
        // 7. receiptNo = base + check (최종 22자리)

        String dateTime = now.format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"));
        String store = String.format("%03d", brandId);
        String pos = String.format("%02d", posId);
        String seq = String.format("%06d", dailySeq);

        String base = dateTime + store + pos + seq; // 21자리
        char check = luhnCheckDigit(base);
        return base + check; // 22자리
    }

    // Luhn(Mod10)
    private char luhnCheckDigit(String digits) {

        int sum = 0;
        boolean doubleIt = true; // 오른쪽에서 두 번째부터 시작하려면 true로 시작
        // 오른쪽부터 처리
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleIt) {
                d = d * 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        int check = (10 - (sum % 10)) % 10;
        return (char) ('0' + check);
    }
}