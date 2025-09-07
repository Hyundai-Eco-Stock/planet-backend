package org.phoenix.planet.constant.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EcoStockError {

    // WebSocket 관련 에러
    WEBSOCKET_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WebSocket 연결에 실패했습니다"),
    WEBSOCKET_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WebSocket 메시지 전송에 실패했습니다"),
    WEBSOCKET_SUBSCRIPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WebSocket 구독에 실패했습니다"),

    // Redis 관련 에러
    REDIS_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 연결에 실패했습니다"),
    REDIS_PUBLISH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 메시지 발행에 실패했습니다"),
    REDIS_SUBSCRIBE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 구독에 실패했습니다"),

    // 주식 데이터 관련 에러
    STOCK_DATA_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "주식 데이터 처리 중 오류가 발생했습니다"),
    STOCK_DATA_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "주식 데이터 검증에 실패했습니다"),
    STOCK_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주식 ID입니다"),
    INVALID_STOCK_PRICE(HttpStatus.BAD_REQUEST, "유효하지 않은 주식 가격입니다"),
    INVALID_TRADE_COUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 거래량입니다"),

    // JSON 관련 에러
    JSON_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 직렬화에 실패했습니다"),
    JSON_DESERIALIZATION_FAILED(HttpStatus.BAD_REQUEST, "JSON 역직렬화에 실패했습니다"),


    // 일반적인 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),


    // =================================================================
    // 주식 판매(SELL_STOCK_TRANSACTION) 프로시저 관련 에러
    // =================================================================
    STOCK_PRICE_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "주식 가격 기록이 존재하지 않습니다."),
    PRICE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 판매가가 현재 시세와 일치하지 않습니다."),
    MEMBER_STOCK_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "판매자가 보유한 주식 정보가 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "보유 주식이 판매 요청 수량보다 적습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String value;

}
