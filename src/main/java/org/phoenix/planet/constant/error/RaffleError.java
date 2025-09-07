package org.phoenix.planet.constant.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum RaffleError {
    // 래플 관련 에러
    RAFFLE_NOT_FOUND(HttpStatus.NOT_FOUND, "래플을 찾을 수 없거나 기간이 만료되었습니다."),
    DUPLICATE_PARTICIPATION(HttpStatus.BAD_REQUEST, "이미 참여한 래플입니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "보유 수량이 부족하거나 해당 상품을 보유하고 있지 않습니다."),
    RAFFLE_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "래플 참여 중 시스템 오류가 발생했습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String value;
}
