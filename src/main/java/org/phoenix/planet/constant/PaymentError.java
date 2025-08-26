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

    // 결제 검증 오류
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
    PAYMENT_ORDER_MISMATCH(HttpStatus.BAD_REQUEST, "주문 정보가 일치하지 않습니다."),
    PAYMENT_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 완료된 결제입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),

    INVALID_PAYMENT_KEY(HttpStatus.BAD_REQUEST, "유효하지 않은 결제 키입니다."),
    INVALID_ORDER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 ID입니다."),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "유효하지 않은 상품 가격입니다."),
    INVALID_PRODUCT_QUANTITY(HttpStatus.BAD_REQUEST, "유효하지 않은 상품 수량입니다."),
    INVALID_ORDER_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 타입입니다."),

    DELIVERY_INFO_REQUIRED(HttpStatus.BAD_REQUEST,"일반 배송 주문의 경우 배송 정보는 필수입니다."),
    PICKUP_INFO_REQUIRED(HttpStatus.BAD_REQUEST,"픽업 배송 주문의 경우 픽업 정보는 필수입니다."),

    // 재고 및 포인트 오류
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "상품 재고가 부족합니다."),
    INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "보유 포인트가 부족합니다."),

    // QR 코드 관련 오류
    QR_CODE_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QR 코드 생성에 실패했습니다."),
    QR_CODE_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 QR 코드입니다."),
    QR_CODE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QR 코드 업로드에 실패했습니다."),

    // 시스템 오류
    PAYMENT_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 시스템 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),

    // 사용자 관련 오류
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "지원하지 않는 결제수단입니다.");

    private final HttpStatus httpStatus;
    private final String value;

}