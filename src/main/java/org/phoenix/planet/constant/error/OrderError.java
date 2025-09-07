package org.phoenix.planet.constant.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderError {

    // 주문 생성 관련 에러
    MIXED_ORDER_TYPE(HttpStatus.BAD_REQUEST, "픽업과 일반배송은 동시 주문할 수 없습니다. 따로 주문해주세요."),
    PICKUP_STORE_REQUIRED(HttpStatus.BAD_REQUEST, "픽업 주문 시 매장 선택은 필수입니다."),
    DELIVERY_STORE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "일반배송 주문에는 매장을 선택할 수 없습니다."),
    DIFFERENT_PICKUP_STORES_NOT_ALLOWED(HttpStatus.BAD_REQUEST,
        "서로 다른 매장의 상품은 함께 주문할 수 없습니다. 매장별로 따로 주문해주세요."),

    // 상품 관련 에러
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    PRODUCT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "현재 판매하지 않는 상품입니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    ECODEAL_PRODUCT_PICKUP_ONLY(HttpStatus.BAD_REQUEST, "에코딜 상품은 픽업만 가능합니다."),
    NORMAL_PRODUCT_DELIVERY_ONLY(HttpStatus.BAD_REQUEST, "일반 상품은 배송만 가능합니다."),
    PICKUP_STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 판매하는 매장을 찾을 수 없습니다."),

    // 주문 상태 관련 에러
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
    ORDER_NOT_CANCELABLE(HttpStatus.BAD_REQUEST, "취소할 수 없는 주문 상태입니다."),
    ORDER_ALREADY_PAID(HttpStatus.BAD_REQUEST, "이미 결제 완료된 주문입니다."),
    ORDER_DRAFT_EXPIRED(HttpStatus.BAD_REQUEST, "주문서 유효시간이 만료되었습니다."),
    UNAUTHORIZED_ORDER_ACCESS(HttpStatus.FORBIDDEN, "본인의 주문서만 조회할 수 있습니다."),
    ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 상품을 찾을 수 없습니다."),

    // 구매확정 관련 에러 (추가)
    ORDER_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "이미 구매확정된 주문입니다."),
    ORDER_NOT_CONFIRMABLE(HttpStatus.BAD_REQUEST, "배송완료된 주문만 구매확정할 수 있습니다."),
    ORDER_STATUS_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "주문 상태 업데이트에 실패했습니다."),
    ORDER_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 픽업완료된 주문입니다."),

    // 포인트 관련 에러
    INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "사용 가능한 포인트가 부족합니다."),
    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "포인트 사용 금액이 올바르지 않습니다."),

    // 기타 에러
    INVALID_ORDER_DATA(HttpStatus.BAD_REQUEST, "주문 데이터가 올바르지 않습니다."),
    ORDER_DRAFT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "주문 생성 중 오류가 발생했습니다."),

    // 에코스톡 관련 에러 (추가)
    ECOSTOCK_PRICE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "에코스톡 가격 정보를 찾을 수 없습니다."),
    ECOSTOCK_ISSUE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "에코스톡 발급에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String value;

}
