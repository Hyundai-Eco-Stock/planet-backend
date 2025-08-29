package org.phoenix.planet.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentError {
    // TossPayments API 관련 오류
    TOSS_API_ERROR(HttpStatus.BAD_GATEWAY, "결제 서비스 연동 중 오류가 발생했습니다."),
    TOSS_PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "결제 승인에 실패했습니다."),
    TOSS_INVALID_RESPONSE(HttpStatus.BAD_GATEWAY, "결제 서비스 응답이 올바르지 않습니다."),
    TOSS_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "결제사 취소 처리에 실패했습니다."),

    // 기본 조회 오류
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),

    // 결제 검증 오류
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
    PAYMENT_ORDER_MISMATCH(HttpStatus.BAD_REQUEST, "주문 정보가 일치하지 않습니다."),
    PAYMENT_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 완료된 결제입니다."),
    INVALID_PAYMENT_KEY(HttpStatus.BAD_REQUEST, "유효하지 않은 결제 키입니다."),
    INVALID_ORDER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 ID입니다."),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "지원하지 않는 결제수단입니다."),

    // 주문 상태별 취소 불가 오류
    PAYMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "결제가 완료되지 않은 주문은 취소할 수 없습니다."),
    ALREADY_ALL_CANCELLED(HttpStatus.BAD_REQUEST, "이미 전체 취소된 주문입니다."),
    ORDER_CONFIRMED(HttpStatus.BAD_REQUEST, "구매 확정된 주문은 취소할 수 없습니다."),
    ORDER_SHIPPED(HttpStatus.BAD_REQUEST, "배송 중인 주문은 취소할 수 없습니다."),
    ORDER_COMPLETED(HttpStatus.BAD_REQUEST, "배송 완료된 주문은 취소할 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "취소할 수 없는 주문 상태입니다."),

    // 결제 상태별 취소 불가 오류
    PAYMENT_ABORTED(HttpStatus.BAD_REQUEST, "결제가 실패한 주문은 취소할 수 없습니다."),
    PAYMENT_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 결제입니다."),

    // 부분 취소 관련 새로 추가
    ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 상품을 찾을 수 없습니다."),
    INVALID_ORDER_PRODUCT(HttpStatus.BAD_REQUEST, "해당 주문서에 속하지 않은 상품입니다."),
    ALREADY_CANCELLED_PRODUCT(HttpStatus.BAD_REQUEST, "이미 취소된 상품입니다."),
    NO_CANCELABLE_PRODUCTS(HttpStatus.BAD_REQUEST, "취소 가능한 상품이 없습니다."),
    PARTIAL_CANCEL_CALCULATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "부분 취소 금액 계산 중 오류가 발생했습니다."),
    INVALID_CANCEL_AMOUNT(HttpStatus.BAD_REQUEST, "잘못된 취소 금액입니다."),

    // 재고 및 포인트 등 오류
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "상품 재고가 부족합니다."),
    STOCK_RESTORE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "재고 복구 처리에 실패했습니다."),

    // 포인트 관련 오류
    INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "보유 포인트가 부족합니다."),
    POINT_REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "포인트 환불 처리에 실패했습니다."),
    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 포인트 금액입니다."),

    // 기부금 관련 오류
    DONATION_CANCEL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기부금 취소 처리에 실패했습니다."),

    // QR 코드 관련 오류
    QR_CODE_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QR 코드 생성에 실패했습니다."),
    QR_CODE_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 QR 코드입니다."),
    QR_CODE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QR 코드 업로드에 실패했습니다."),

    // 시스템 및 데이터베이스 오류
    PAYMENT_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 시스템 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),
    DATABASE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 업데이트에 실패했습니다."),
    ORDER_STATUS_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "주문 상태 업데이트에 실패했습니다."),
    PAYMENT_STATUS_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제 상태 업데이트에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String value;

}