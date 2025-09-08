CREATE OR REPLACE PROCEDURE SELL_STOCK_USER_TRANSACTION(
    p_member_id IN NUMBER,
    p_eco_stock_id IN NUMBER,
    p_sell_quantity IN NUMBER,
    p_executed_price IN NUMBER,  -- Redis에서 받은 체결가
    p_stock_price_history_id IN NUMBER,  -- Redis에서 받은 히스토리 ID
    p_success OUT NUMBER,
    p_message OUT VARCHAR2,
    p_transaction_time OUT DATE,
    p_current_total_quantity OUT NUMBER,  -- 계산 후 보유 수량
    p_current_total_amount OUT NUMBER,    -- 계산 후 보유 금액
    p_member_point OUT NUMBER             -- 계산 후 멤버 포인트
)
IS
    v_member_stock_info_id NUMBER;
    v_member_stock_info MEMBER_STOCK_INFO%ROWTYPE;
    v_new_quantity NUMBER;
    v_new_amount NUMBER;
    v_point_earned NUMBER;
BEGIN
    p_transaction_time := SYSDATE;

    -- 1. 판매자 주식 정보 조회
    BEGIN
        SELECT *
        INTO v_member_stock_info
        FROM MEMBER_STOCK_INFO
        WHERE MEMBER_ID = p_member_id
          AND ECO_STOCK_ID = p_eco_stock_id;

        v_member_stock_info_id := v_member_stock_info.MEMBER_STOCK_INFO_ID;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_success := -3;
            p_message := '판매자가 보유한 주식 정보가 없습니다.';
            ROLLBACK; RETURN;
    END;


    -- 새로운 수량과 금액 계산
    v_new_quantity := v_member_stock_info.CURRENT_TOTAL_QUANTITY - p_sell_quantity;
    v_new_amount := v_member_stock_info.CURRENT_TOTAL_AMOUNT *
                    (v_new_quantity / v_member_stock_info.CURRENT_TOTAL_QUANTITY);
    v_point_earned := p_sell_quantity * p_executed_price;

    -- 2. 보유 주식 수량 차감
    UPDATE MEMBER_STOCK_INFO
    SET CURRENT_TOTAL_QUANTITY = v_new_quantity,
        CURRENT_TOTAL_AMOUNT = v_new_amount,
        UPDATED_AT = SYSDATE
    WHERE MEMBER_STOCK_INFO_ID = v_member_stock_info_id;

    -- 3. 거래 내역 기록
    INSERT INTO TRANSACTION_HISTORY (
        TRANSACTION_HISTORY_ID,
        MEMBER_STOCK_INFO_ID,
        STOCK_PRICE_HISTORY_ID,
        SELL_COUNT,
        SELL_PRICE,
        CREATED_AT
    ) VALUES (
        SEQ_TRANSACTION_HISTORY.NEXTVAL,
        v_member_stock_info_id,
        p_stock_price_history_id,
        p_sell_quantity,
        p_executed_price,
        SYSDATE
    );

    -- 4. 포인트 변동 내역 기록
    INSERT INTO POINT_EXCHANGE_HISTORY (
        POINT_EXCHANGE_HISTORY_ID,
        POINT_PRICE,
        STATUS,
        MEMBER_ID,
        CREATED_AT
    ) VALUES (
        SEQ_POINT_EXchange_HISTORY.NEXTVAL,
        v_point_earned,
        'ADD',
        p_member_id,
        SYSDATE
    );

    -- 5. 사용자 포인트 업데이트
    UPDATE MEMBER
    SET point = point + v_point_earned,
        UPDATED_AT = SYSDATE
    WHERE member_id = p_member_id
    RETURNING point INTO p_member_point;  -- 업데이트된 포인트 반환

    -- OUT 파라미터에 계산 결과 설정
    p_current_total_quantity := v_new_quantity;
    p_current_total_amount := v_new_amount;

    -- 성공 처리
    p_success := 1;
    p_message := '판매가 성공적으로 완료되었습니다.';
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        p_success := -99;
        p_message := '처리 중 오류 발생: ' || SQLERRM;
        ROLLBACK;
END SELL_STOCK_USER_TRANSACTION;