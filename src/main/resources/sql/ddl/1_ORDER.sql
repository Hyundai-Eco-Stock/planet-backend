-- ------------------------- 주문/결제 -------------------------
CREATE TABLE order_history
(
    order_history_id    NUMBER               NOT NULL,
    order_number        VARCHAR2(500)        NOT NULL, -- 주문 번호 (random)
    order_status        VARCHAR2(255)        NOT NULL,
    origin_price        NUMBER(10)           NOT NULL, -- 총 가격
    used_point          NUMBER(10),                    -- 사용 포인트
    donation_price      NUMBER(10),                    -- 기부 금액
    final_pay_price     NUMBER(10)           NOT NULL, -- 결제할 금액: (총 가격) - (사용 포인트) + (기부 금액)
    eco_deal_qr_url     VARCHAR2(500),
    member_id           NUMBER               NOT NULL,
    department_store_id VARCHAR2(255)        NOT NULL,
    created_at          DATE DEFAULT SYSDATE NOT NULL,
    updated_at          DATE,
    CONSTRAINT PK_ORDER_HISTORY PRIMARY KEY (order_history_id),
    CONSTRAINT ck_order_status CHECK (order_status IN
                                      ('PENDING', 'PAID', 'SHIPPED', 'COMPLETED', 'ALL_CANCELLED',
                                       'PARTIAL_CANCELLED')),
    CONSTRAINT fk_order_member FOREIGN KEY (member_id)
        REFERENCES member (member_id),
    CONSTRAINT fk_order_department_store FOREIGN KEY (department_store_id)
        REFERENCES department_store (department_store_id)
);
CREATE SEQUENCE seq_order_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE order_product
(
    order_product_id NUMBER               NOT NULL,
    price            NUMBER               NOT NULL, -- 할인 적용 금액
    quantity         NUMBER               NOT NULL,
    cancel_status    VARCHAR2(100)        NOT NULL, -- (부분) 취소 여부
    order_type       VARCHAR2(255)        NOT NULL, -- 배송, 픽업 여부
    order_history_id NUMBER               NOT NULL,
    product_id       NUMBER               NOT NULL,
    created_at       DATE DEFAULT SYSDATE NOT NULL,
    updated_at       DATE,
    CONSTRAINT PK_ORDER_PRODUCT PRIMARY KEY (order_product_id),
    CONSTRAINT ck_order_product_cancel_status CHECK (cancel_status IN ('Y', 'N')),
    CONSTRAINT ck_order_product_order_type CHECK (order_type IN ('PICKUP', 'DELIVERY')),
    CONSTRAINT fk_order_product_order FOREIGN KEY (order_history_id)
        REFERENCES order_history (order_history_id),
    CONSTRAINT fk_order_product_product FOREIGN KEY (product_id)
        REFERENCES product (product_id)
);
CREATE SEQUENCE seq_order_product START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE payment_history
(
    payment_id       NUMBER               NOT NULL,
    payment_key      VARCHAR2(255)        NOT NULL, -- PG사 결제 id
    payment_method   VARCHAR2(255)        NOT NULL, -- 결제 종류
    payment_status   VARCHAR2(255)        NOT NULL, -- 최종 결제 상태
    order_history_id NUMBER               NOT NULL,
    created_at       DATE DEFAULT SYSDATE NOT NULL,
    updated_at       DATE,
    CONSTRAINT PK_PAYMENT_HISTORY PRIMARY KEY (payment_id),
    CONSTRAINT ck_payment_history_method CHECK (payment_method IN ('CREDIT_CARD', 'KAKAOPAY')),
    CONSTRAINT ck_payment_history_status CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    CONSTRAINT fk_payment_history_order FOREIGN KEY (order_history_id)
        REFERENCES order_history (order_history_id)
);
CREATE SEQUENCE seq_payment_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

