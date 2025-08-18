package org.phoenix.planet.dto.payment.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.phoenix.planet.constant.PaymentMethod;
import org.phoenix.planet.constant.PaymentStatus;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistory {

    private Long paymentId;

    private String paymentKey;  // TossPayments 결제 키 (결제 조회/취소 시 사용)

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private Long orderHistoryId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
