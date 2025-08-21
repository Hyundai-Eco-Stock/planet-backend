-- 오프라인 매장 테이블
CREATE TABLE offline_shop
(
    offline_shop_id     NUMBER               NOT NULL,
    department_store_id NUMBER               NOT NULL,
    name                VARCHAR2(255)        NOT NULL,
    type                VARCHAR2(50)         NOT NULL,
    created_at          DATE DEFAULT SYSDATE NOT NULL,
    updated_at          DATE,
    CONSTRAINT PK_OFFLINE_SHOP PRIMARY KEY (offline_shop_id),
    CONSTRAINT FK_OFFLINE_SHOP_DEPARTMENT FOREIGN KEY (department_store_id)
        REFERENCES department_store (department_store_id)
);

-- --------------------------- 오프라인 샵 상품 -----------------------------
CREATE TABLE offline_product
(
    offline_product_id NUMBER PRIMARY KEY,
    offline_shop_id    NUMBER               NOT NULL,
    name               VARCHAR2(255)        NOT NULL,
    price              NUMBER               NOT NULL,
    created_at         DATE DEFAULT SYSDATE NOT NULL,
    updated_at         DATE,
    CONSTRAINT fk_offline_shop_product_shop FOREIGN KEY (offline_shop_id)
        REFERENCES offline_shop (offline_shop_id)
);

-- 시퀀스 생성
CREATE SEQUENCE seq_offline_product START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 시퀀스 생성
CREATE SEQUENCE seq_offline_shop START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

DROP TABLE card_company;
-- 카드 회사
CREATE TABLE card_company
(
    card_company_id NUMBER PRIMARY KEY,
    name            VARCHAR2(255)
);
CREATE SEQUENCE seq_card_company START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 멤버 소유 카드 정보
CREATE TABLE member_card
(
    member_card_id  NUMBER PRIMARY KEY,
    member_id       NUMBER,
    card_company_id NUMBER,
    card_number     VARCHAR2(255),
    created_at      DATE DEFAULT SYSDATE NOT NULL,
    updated_at      DATE,
    CONSTRAINT fk_member_card_member FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT fk_member_card_company FOREIGN KEY (card_company_id) REFERENCES card_company (card_company_id)
);
CREATE SEQUENCE seq_member_card START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-------------------------------------------------------
-- 오프라인 결제 내역 (결제 단위, 1건당 1 row)
-------------------------------------------------------
CREATE TABLE offline_pay_history
(
    offline_pay_history_id NUMBER PRIMARY KEY,
    shop_id                NUMBER                    NOT NULL,
    card_company_id        NUMBER                    NOT NULL,
    card_number_last4      VARCHAR2(4)               NOT NULL, -- 카드 번호 끝 4자리
    total_price            NUMBER                    NOT NULL, -- 결제 총액
    paid_at                DATE      DEFAULT SYSDATE NOT NULL,
    barcode                VARCHAR2(255)             NOT NULL, -- 바코드
    stock_issued           NUMBER(1) DEFAULT 0       NOT NULL, -- 0: 미지급, 1: 지급
    created_at             DATE      DEFAULT SYSDATE NOT NULL,
    updated_at             DATE,
    CONSTRAINT fk_offline_pay_shop
        FOREIGN KEY (shop_id) REFERENCES offline_shop (offline_shop_id),
    CONSTRAINT fk_offline_pay_card_company
        FOREIGN KEY (card_company_id) REFERENCES card_company (card_company_id),
    CONSTRAINT chk_stock_issued
        CHECK (stock_issued IN (0, 1))
);

CREATE SEQUENCE seq_offline_pay_history
    START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-------------------------------------------------------
-- 오프라인 결제 상품 (결제된 상품별 라인 아이템)
-------------------------------------------------------
CREATE TABLE offline_pay_product
(
    offline_pay_product_id NUMBER PRIMARY KEY,
    offline_pay_history_id NUMBER               NOT NULL,
    product_id             NUMBER,                        -- offline_product_id 참조
    name                   VARCHAR2(500)        NOT NULL, -- 당시 상품명 (가격 변경 대비 스냅샷)
    price                  NUMBER               NOT NULL, -- 당시 단가
    amount                 NUMBER               NOT NULL, -- 수량
    created_at             DATE DEFAULT SYSDATE NOT NULL,
    updated_at             DATE,
    CONSTRAINT fk_offline_pay_product_history
        FOREIGN KEY (offline_pay_history_id) REFERENCES offline_pay_history (offline_pay_history_id),
    CONSTRAINT fk_offline_pay_product
        FOREIGN KEY (product_id) REFERENCES offline_product (offline_product_id)
);

CREATE SEQUENCE seq_offline_pay_product
    START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;