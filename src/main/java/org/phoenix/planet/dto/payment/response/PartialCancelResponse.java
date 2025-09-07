package org.phoenix.planet.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import org.phoenix.planet.constant.order.OrderStatus;

/**
 * 부분 취소 응답 DTO (새로 생성)
 */
public record PartialCancelResponse(
    boolean success,
    String message,
    Long orderHistoryId,
    String orderNumber,
    Long cancelAmount,
    Long refundedPoints,
    Long refundedDonation,
    OrderStatus newOrderStatus,
    List<CanceledProductDetail> canceledProducts,  // 취소된 상품 상세 정보

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime cancelledAt
) {

    /**
     * 취소된 상품 상세 정보
     */
    public record CanceledProductDetail(
        Long orderProductId,
        Long productId,
        String productName,
        Integer quantity,
        Integer unitPrice, // 원가 단가
        Integer discountAmount,
        Long refundAmount
    ) {

    }

    /**
     * 성공 응답 생성
     */
    public static PartialCancelResponse success(
        Long orderHistoryId,
        String orderNumber,
        Long cancelAmount,
        Long refundedPoints,
        Long refundedDonation,
        OrderStatus newOrderStatus,
        List<CanceledProductDetail> canceledProducts,
        String message
    ) {

        return new PartialCancelResponse(
            true,
            message,
            orderHistoryId,
            orderNumber,
            cancelAmount,
            refundedPoints,
            refundedDonation,
            newOrderStatus,
            canceledProducts,
            LocalDateTime.now()
        );
    }

    /**
     * 실패 응답 생성
     */
    public static PartialCancelResponse error(String message) {

        return new PartialCancelResponse(
            false,
            message,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

}