-- 오프라인 매장 테이블
DROP TABLE offline_shop;
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

-- --------------------------- 오프라인 결제 정보 -----------------------------
CREATE TABLE offline_pay_history
(
    offline_pay_history_id NUMBER PRIMARY KEY,
    price                  NUMBER               NOT NULL,
    paid_at                DATE                 NOT NULL,
    member_id              NUMBER               NOT NULL,
    barcode                VARCHAR2(255)        NOT NULL,
    created_at             DATE DEFAULT SYSDATE NOT NULL,
    updated_at             DATE,
    CONSTRAINT fk_offline_pay_history_member FOREIGN KEY (member_id) REFERENCES member (member_id)
);
CREATE SEQUENCE seq_offline_pay_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE offline_pay_product_history
(
    pay_product_offline_history_id NUMBER PRIMARY KEY,
    product_name                   VARCHAR2(500)        NOT NULL,
    product_price                  NUMBER               NOT NULL,
    amount                         NUMBER               NOT NULL,
    offline_pay_history_id         NUMBER               NOT NULL,
    created_at                     DATE DEFAULT SYSDATE NOT NULL,
    updated_at                     DATE,

    CONSTRAINT fk_offline_prod_pay
        FOREIGN KEY (offline_pay_history_id) REFERENCES offline_pay_history (offline_pay_history_id)
);
CREATE SEQUENCE seq_offline_pay_product_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
