CREATE OR REPLACE PROCEDURE PARTICIPATE_RAFFLE(
    p_raffle_id IN NUMBER,
    p_member_id IN NUMBER,
    p_result OUT NUMBER -- -2: 래플없음/기간만료, -3: 중복참여, -5: 수량부족/미보유, 1: 성공
)
AS
    v_updated_rows         NUMBER;
    v_eco_stock_amount     NUMBER;
    v_eco_stock_id         NUMBER;
    v_already_participated NUMBER;
BEGIN
    -- 1. 래플 정보 조회 (존재 + 기간 검증 동시에) 데이터 못찾으면 NO_DATA_FOUND -> -2
    SELECT ECO_STOCK_AMOUNT, ECO_STOCK_ID
    INTO v_eco_stock_amount, v_eco_stock_id
    FROM RAFFLE
    WHERE RAFFLE_ID = p_raffle_id
      AND SYSDATE BETWEEN START_DATE AND END_DATE;

    -- 2. 원자적 업데이트 (중복 참여 방지 포함)
    UPDATE MEMBER_STOCK_INFO
    SET CURRENT_TOTAL_QUANTITY = CURRENT_TOTAL_QUANTITY - v_eco_stock_amount,
        CURRENT_TOTAL_AMOUNT   = CASE
                                     WHEN CURRENT_TOTAL_QUANTITY > 0 THEN
                                         ROUND(CURRENT_TOTAL_AMOUNT -
                                               (CURRENT_TOTAL_AMOUNT / CURRENT_TOTAL_QUANTITY * v_eco_stock_amount))
                                     ELSE 0
            END,
        UPDATED_AT             = SYSDATE
    WHERE MEMBER_ID = p_member_id
      AND ECO_STOCK_ID = v_eco_stock_id
      AND CURRENT_TOTAL_QUANTITY >= v_eco_stock_amount
      AND NOT EXISTS (SELECT 1 FROM RAFFLE_HISTORY WHERE RAFFLE_ID = p_raffle_id AND MEMBER_ID = p_member_id);

    v_updated_rows := SQL%ROWCOUNT;

    IF v_updated_rows > 0 THEN
        INSERT INTO RAFFLE_HISTORY (RAFFLE_HISTORY_ID, RAFFLE_ID, MEMBER_ID)
        VALUES (SEQ_RAFFLE_HISTORY.NEXTVAL, p_raffle_id, p_member_id);

        p_result := 1;
        COMMIT;
    ELSE
        -- 실패 원인 구분
        SELECT COUNT(*)
        INTO v_already_participated
        FROM RAFFLE_HISTORY
        WHERE RAFFLE_ID = p_raffle_id
          AND MEMBER_ID = p_member_id;

        IF v_already_participated > 0 THEN
            p_result := -3; -- 중복 참여
        ELSE
            p_result := -5; -- 수량 부족/미보유
        END IF;
        ROLLBACK;
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_result := -2; -- 래플없음 또는 기간만료
        ROLLBACK;
    WHEN OTHERS THEN
        p_result := 0; -- 시스템 에러
        ROLLBACK;
        RAISE;
END PARTICIPATE_RAFFLE;
/