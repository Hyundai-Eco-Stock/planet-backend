package org.phoenix.planet.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.error.payment.PaymentException;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CARD("카드", "CARD"),
    EASY_PAY("간편결제", "EASY_PAY"),
    MOBILE_PHONE("휴대폰", "MOBILE_PHONE"),
    TRANSFER("계좌이체", "TRANSFER");

    private final String koreanName;
    private final String englishCode;

    /**
     * TossPayments API 응답의 한글 결제수단명을 enum으로 변환
     */
    @JsonCreator
    public static PaymentMethod fromKoreanName(String koreanName) {
        if (koreanName == null || koreanName.trim().isEmpty()) {
            throw new PaymentException(PaymentError.INVALID_PAYMENT_METHOD);
        }

        for (PaymentMethod method : values()) {
            if (method.getKoreanName().equals(koreanName)) {
                return method;
            }
        }

        throw new PaymentException(PaymentError.INVALID_PAYMENT_METHOD);
    }

    /**
     * 영문 코드로 enum 찾기
     */
    public static PaymentMethod fromEnglishCode(String englishCode) {
        if (englishCode == null || englishCode.trim().isEmpty()) {
            throw new PaymentException(PaymentError.INVALID_PAYMENT_METHOD);
        }

        for (PaymentMethod method : values()) {
            if (method.getEnglishCode().equals(englishCode)) {
                return method;
            }
        }

        throw new PaymentException(PaymentError.INVALID_PAYMENT_METHOD);
    }

    /**
     * JSON 직렬화 시 영문 코드 사용
     */
    @JsonValue
    public String toJsonValue() {
        return this.englishCode;
    }

    /**
     * 한글명 반환 (화면 표시용)
     */
    public String getDisplayName() {
        return this.koreanName;
    }
}