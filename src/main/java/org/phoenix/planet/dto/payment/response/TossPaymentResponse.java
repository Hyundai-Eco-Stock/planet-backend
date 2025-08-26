package org.phoenix.planet.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * TossPayments API 응답 DTO (실제 API 스펙 기준)
 */
public record TossPaymentResponse(
        // 기본 정보
        String paymentKey,           // 결제 키값 (최대 200자)
        String orderId,              // 주문번호
        String orderName,            // 구매상품 (최대 100자)
        String mId,                  // 상점아이디

        // 결제 방법 - 한글로 응답됨 ("카드", "가상계좌", "간편결제" 등)
        @JsonProperty("method")
        String method,

        // 금액 정보
        Integer totalAmount,         // 총 결제 금액
        Integer balanceAmount,       // 취소 가능 금액(잔고)
        Integer suppliedAmount,      // 공급가액
        Integer vat,                // 부가세

        // 상태 및 시간
        String status,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime requestedAt,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime approvedAt,

        // 결제수단별 정보
        CardInfo card,
        MobilePhoneInfo mobilePhone,
        TransferInfo transfer,
        EasyPayInfo easyPay,

        // 취소 정보
        List<CancelInfo> cancels,
        Boolean isPartialCancelable, // 부분 취소 가능 여부

        // 영수증 및 기타
        ReceiptInfo receipt,
        FailureInfo failure         // 결제 실패 정보
) {

    public record CardInfo(
            Integer amount,              // 카드사 결제 요청 금액
            String issuerCode,           // 카드 발급사 코드
            String acquirerCode,         // 매입사 코드
            String number,               // 카드번호 (마스킹)
            Integer installmentPlanMonths, // 할부 개월 수
            String approveNo,            // 승인번호
            Boolean useCardPoint,        // 카드포인트 사용 여부
            String cardType,             // 신용, 체크, 기프트, 미확인
            String ownerType,            // 개인, 법인, 미확인
            String acquireStatus,        // 매입 상태 (READY, REQUESTED, COMPLETED, etc.)
            Boolean isInterestFree      // 무이자 할부 여부
    ) {}

    public record MobilePhoneInfo(
            String customerMobilePhone,  // 구매자 휴대폰 번호
            String settlementStatus,     // 정산 상태
            String receiptUrl           // 영수증 URL
    ) {}

    public record TransferInfo(
            String bankCode,
            String settlementStatus
    ) {}

    public record EasyPayInfo(
            String provider,            // 간편결제사 코드
            Integer amount,             // 간편결제 금액
            Integer discountAmount      // 즉시 할인 금액
    ) {}

    public record CancelInfo(
            Integer cancelAmount,        // 취소 금액
            String cancelReason,         // 취소 사유

            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            OffsetDateTime canceledAt,    // 취소 시점

            String transactionKey,       // 취소 거래 키
            String cancelStatus        // DONE
    ) {}

    public record ReceiptInfo(
            String url                  // 영수증 URL
    ) {}

    public record FailureInfo(
            String code,                // 에러 코드
            String message              // 에러 메시지
    ) {}

}
