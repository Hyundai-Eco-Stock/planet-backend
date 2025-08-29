CREATE OR REPLACE PROCEDURE SELL_STOCK_TRANSACTION(
    p_member_id IN NUMBER,
    p_eco_stock_id IN NUMBER,
    p_sell_quantity IN NUMBER,
    p_sell_price IN NUMBER,
    p_success OUT NUMBER, -- 성공: 1, 실패: 음수 에러 코드
    p_message OUT VARCHAR2
)
    IS
    v_latest_price           NUMBER;
    v_stock_price_history_id NUMBER;
    v_member_stock_info_id   NUMBER;
BEGIN
    -- 1-1. 최신 시세 정보 조회
    BEGIN
        SELECT STOCK_PRICE, STOCK_PRICE_HISTORY_ID
        INTO v_latest_price, v_stock_price_history_id
        FROM (SELECT STOCK_PRICE, STOCK_PRICE_HISTORY_ID
              FROM STOCK_PRICE_HISTORY
              WHERE ECO_STOCK_ID = p_eco_stock_id
              ORDER BY STOCK_TIME DESC)
        WHERE ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_success := -1; -- 에러 코드 -1
            p_message := '주식 가격 기록이 존재하지 않습니다.';
            ROLLBACK; RETURN;
    END;

    -- 1-2. 요청 가격 비교
    IF v_latest_price != p_sell_price THEN
        p_success := -2; -- 에러 코드 -2
        p_message := '요청 판매가가 현재 시세(' || v_latest_price || ')와 일치하지 않습니다.';
        ROLLBACK; RETURN;
    END IF;

    -- 1-3. 판매자 주식 정보 ID 조회
    BEGIN
        SELECT MEMBER_STOCK_INFO_ID
        INTO v_member_stock_info_id
        FROM MEMBER_STOCK_INFO
        WHERE MEMBER_ID = p_member_id
          AND ECO_STOCK_ID = p_eco_stock_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_success := -3; -- 에러 코드 -3
            p_message := '판매자가 보유한 주식 정보가 없습니다.';
            ROLLBACK; RETURN;
    END;

    -- 2-1. 보유 주식 수량 차감
    UPDATE MEMBER_STOCK_INFO
    SET CURRENT_TOTAL_QUANTITY = CURRENT_TOTAL_QUANTITY - p_sell_quantity,
        CURRENT_TOTAL_AMOUNT   = ROUND(CURRENT_TOTAL_AMOUNT *
                                       ((CURRENT_TOTAL_QUANTITY - p_sell_quantity) / CURRENT_TOTAL_QUANTITY)),
        UPDATED_AT             = sysdate
    WHERE MEMBER_STOCK_INFO_ID = v_member_stock_info_id
      AND CURRENT_TOTAL_QUANTITY >= p_sell_quantity;

    IF SQL%ROWCOUNT = 0 THEN
        p_success := -4; -- 에러 코드 -4
        p_message := '보유 주식이 판매 요청 수량보다 적습니다.';
        ROLLBACK; RETURN;
    END IF;

    -- 2-2. 거래 내역 기록
    INSERT INTO TRANSACTION_HISTORY (TRANSACTION_HISTORY_ID, MEMBER_STOCK_INFO_ID, STOCK_PRICE_HISTORY_ID, SELL_COUNT,
                                     SELL_PRICE)
    VALUES (SEQ_TRANSACTION_HISTORY.NEXTVAL, v_member_stock_info_id, v_stock_price_history_id, p_sell_quantity,
            p_sell_price);

    -- 2-3. 포인트 변동 내역 기록
    INSERT INTO POINT_EXCHANGE_HISTORY (POINT_EXCHANGE_HISTORY_ID, POINT_PRICE, STATUS, MEMBER_ID)
    VALUES (SEQ_POINT_EXchange_HISTORY.NEXTVAL, (p_sell_quantity * p_sell_price), 'ADD', p_member_id);

    -- 2-4. 사용자 포인트 업데이트
    UPDATE MEMBER
    SET point     = point + (p_sell_quantity * p_sell_price),
        UPDATED_AT=sysdate
    WHERE member_id = p_member_id;

    -- 3. 성공 처리
    p_success := 1; -- 성공 코드 1
    p_message := '판매가 성공적으로 완료되었습니다.';
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        p_success := -99; -- 에러 코드 -99
        p_message := '처리 중 오류 발생: ' || SQLERRM;
        ROLLBACK;
END SELL_STOCK_TRANSACTION;
/

commit;