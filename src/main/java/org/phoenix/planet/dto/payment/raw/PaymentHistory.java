package org.phoenix.planet.dto.payment.raw;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.phoenix.planet.constant.payment.PaymentMethod;
import org.phoenix.planet.constant.payment.PaymentStatus;
import org.phoenix.planet.dto.payment.response.TossPaymentResponse;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistory {

    private Long paymentId;
    private Long orderHistoryId;

    // === TossPayments 핵심 정보 ===
    private String paymentKey;
    private String orderId;
    private Integer totalAmount;
    private PaymentMethod method;
    private PaymentStatus status;

    // === 시간 정보 ===
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;

    // === 취소 관련 ===
    private Integer balanceAmount;

    // === 부가 정보 ===
    private String receiptUrl;
    private String failureCode;
    private String failureMessage;

    // === 시스템 정보 ===
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * TossPaymentResponse로부터 PaymentHistory 생성
     */
    public static PaymentHistory fromTossResponse(TossPaymentResponse response,
        Long orderHistoryId) {

        return PaymentHistory.builder()
            .orderHistoryId(orderHistoryId)
            .paymentKey(response.paymentKey())
            .orderId(response.orderId())
            .totalAmount(response.totalAmount())
            .method(PaymentMethod.fromKoreanName(response.method()))
            .status(PaymentStatus.valueOf(response.status()))
            .requestedAt(
                response.requestedAt() != null ? response.requestedAt().toLocalDateTime() : null)
            .approvedAt(
                response.approvedAt() != null ? response.approvedAt().toLocalDateTime() : null)
            .balanceAmount(response.balanceAmount())
            .receiptUrl(response.receipt() != null ? response.receipt().url() : null)
            .failureCode(response.failure() != null ? response.failure().code() : null)
            .failureMessage(response.failure() != null ? response.failure().message() : null)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 결제 완료 여부
     */
    public boolean isPaymentCompleted() {

        return PaymentStatus.DONE.equals(this.status);
    }

    /**
     * 취소 가능 여부
     */
    public boolean isCancelable() {

        return isPaymentCompleted() && (this.balanceAmount != null && this.balanceAmount > 0);
    }

}