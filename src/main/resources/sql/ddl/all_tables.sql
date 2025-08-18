-- ------------------------------ 멤버 ------------------------------
CREATE TABLE member
(
    member_id      NUMBER               NOT NULL,
    email          VARCHAR2(511)        NOT NULL,
    name           VARCHAR2(511)        NOT NULL,
    profile_url    VARCHAR2(1000),
    pwd_hash       VARCHAR2(1000),
    provider       VARCHAR2(255),
    birth          DATE,
    sex            VARCHAR2(100),
    address        VARCHAR2(100),
    detail_address VARCHAR2(100),
    role           VARCHAR2(50),
    created_at     DATE DEFAULT SYSDATE NOT NULL,
    updated_at     DATE,
    CONSTRAINT PK_MEMBER PRIMARY KEY (member_id),
    CONSTRAINT ck_member_sex CHECK (sex IN ('M', 'F')),
    CONSTRAINT ux_member_email UNIQUE (email)
);
CREATE SEQUENCE seq_member START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- ------------------------------ 상품 ------------------------------
CREATE TABLE brand
(
    brand_id   NUMBER               NOT NULL,
    name       VARCHAR2(255)        NOT NULL,
    image_url  VARCHAR2(511),
    created_at DATE DEFAULT SYSDATE NOT NULL,
    updated_at DATE,
    CONSTRAINT PK_BRAND PRIMARY KEY (brand_id)
);
CREATE SEQUENCE seq_brand START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE product_category
(
    category_id NUMBER               NOT NULL,
    name        VARCHAR2(255)        NOT NULL,
    created_at  DATE DEFAULT SYSDATE NOT NULL,
    updated_at  DATE,
    CONSTRAINT PK_PRODUCT_CATEGORY PRIMARY KEY (category_id)
);
CREATE SEQUENCE seq_product_category START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- ---------------------------- 에코 스톡 ----------------------------
CREATE TABLE eco_stock
(
    eco_stock_id NUMBER               NOT NULL,
    name         VARCHAR2(255)        NOT NULL,
    quantity     NUMBER               NOT NULL,
    image_url    VARCHAR2(255)        NULL,
    created_at   DATE DEFAULT SYSDATE NOT NULL,
    updated_at   DATE,
    CONSTRAINT PK_ECO_STOCK PRIMARY KEY (eco_stock_id)
);
CREATE SEQUENCE seq_eco_stock START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- 고객의 주식 보유량
CREATE TABLE member_stock_info
(
    member_stock_info_id   NUMBER               NOT NULL,
    member_id              NUMBER               NOT NULL,
    eco_stock_id           NUMBER               NOT NULL,
    current_total_quantity NUMBER,
    current_total_amount   NUMBER,
    created_at             DATE DEFAULT SYSDATE NOT NULL,
    updated_at             DATE,
    CONSTRAINT PK_MEMBER_STOCK_INFO PRIMARY KEY (member_stock_info_id),
    CONSTRAINT fk_member_stock_member FOREIGN KEY (member_id)
        REFERENCES member (member_id),
    CONSTRAINT fk_member_stock_eco FOREIGN KEY (eco_stock_id)
        REFERENCES eco_stock (eco_stock_id)
);
CREATE SEQUENCE seq_member_stock_info START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- 실시간 에코스톡 가격 기록
CREATE TABLE stock_price_history
(
    stock_price_history_id NUMBER               NOT NULL,
    stock_time             DATE                 NOT NULL,
    stock_price            NUMBER               NOT NULL,
    eco_stock_id           NUMBER               NOT NULL,
    created_at             DATE DEFAULT SYSDATE NOT NULL,
    updated_at             DATE,
    CONSTRAINT PK_STOCK_PRICE_HISTORY PRIMARY KEY (stock_price_history_id),
    CONSTRAINT fk_stock_price_history_eco FOREIGN KEY (eco_stock_id)
        REFERENCES eco_stock (eco_stock_id)
);
CREATE SEQUENCE seq_stock_price_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- 에코스톡 포인트 교환 기록
CREATE TABLE transaction_history
(
    transaction_history_id NUMBER               NOT NULL,
    sell_price             NUMBER               NOT NULL,
    sell_count             NUMBER               NOT NULL,
    member_stock_info_id   NUMBER               NOT NULL,
    stock_price_history_id NUMBER               NOT NULL,
    created_at             DATE DEFAULT SYSDATE NOT NULL,
    updated_at             DATE,
    CONSTRAINT PK_TRANSACTION_HISTORY PRIMARY KEY (transaction_history_id),
    CONSTRAINT fk_transaction_history_member_stock FOREIGN KEY (member_stock_info_id)
        REFERENCES member_stock_info (member_stock_info_id),
    CONSTRAINT fk_transaction_history_price_history FOREIGN KEY (stock_price_history_id)
        REFERENCES stock_price_history (stock_price_history_id)
);
CREATE SEQUENCE seq_transaction_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- 에코스톡 발행
CREATE TABLE stock_issue
(
    stock_issue_id NUMBER               NOT NULL,
    start_price    NUMBER               NOT NULL,
    status         VARCHAR2(255)        NOT NULL,
    source_type    VARCHAR2(255)        NOT NULL,
    member_id      NUMBER               NOT NULL,
    eco_stock_id   NUMBER               NOT NULL,
    created_at     DATE DEFAULT SYSDATE NOT NULL,
    updated_at     DATE,
    CONSTRAINT PK_STOCK_ISSUE PRIMARY KEY (stock_issue_id),
    CONSTRAINT ck_stock_issue_status CHECK (status IN ('ISSUED', 'CANCELLED', 'REDEEMED')),
    CONSTRAINT ck_stock_issue_source_type CHECK (source_type IN
                                                 ('QR', 'EV_PARK', 'RECEIPT', 'TUMBLER', 'ETC')),
    CONSTRAINT fk_stock_issue_member FOREIGN KEY (member_id)
        REFERENCES member (member_id),
    CONSTRAINT fk_stock_issue_eco FOREIGN KEY (eco_stock_id)
        REFERENCES eco_stock (eco_stock_id)
);
CREATE SEQUENCE seq_stock_issue START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- ----------------------- 상품/장바구니 (에코딜) -----------------------
CREATE TABLE department_store
(
    department_store_id NUMBER               NOT NULL,
    name                VARCHAR2(255)        NOT NULL,
    lat                 VARCHAR2(255)        NOT NULL,
    lng                 VARCHAR2(255)        NOT NULL,
    created_at          DATE DEFAULT SYSDATE NOT NULL,
    updated_at          DATE,
    CONSTRAINT PK_DEPARTMENT_STORE PRIMARY KEY (department_store_id)
);
CREATE SEQUENCE seq_department_store START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- TODO: 향후 NOT NULL로 수정
-- TODO: check 추가 필요
CREATE TABLE product
(
    product_id      NUMBER                 NOT NULL,
    category_id     NUMBER                 NULL,
    brand_id        NUMBER                 NULL,
    name            VARCHAR2(255)          NULL,
    price           NUMBER                 NULL,
    image_url       VARCHAR2(1000)         NULL,
    quantity        NUMBER DEFAULT 99999   NOT NULL, -- 재고 수량
    eco_deal_status VARCHAR2(100),
    sale_percent    NUMBER,
    created_at      DATE   DEFAULT SYSDATE NOT NULL,
    updated_at      DATE,
    CONSTRAINT PK_PRODUCT PRIMARY KEY (product_id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id)
        REFERENCES product_category (category_id),
    CONSTRAINT fk_product_brand FOREIGN KEY (brand_id)
        REFERENCES brand (brand_id)
);
CREATE SEQUENCE seq_product START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE product_image
(
    product_image_id NUMBER               NOT NULL,
    product_id       NUMBER               NOT NULL,
    image_url        VARCHAR2(500)        NOT NULL,
    sort_order       NUMBER               NOT NULL, -- 1,2,3...
    alt_text         VARCHAR2(255),
    created_at       DATE DEFAULT SYSDATE NOT NULL,
    updated_at       DATE,
    CONSTRAINT PK_PRODUCT_IMAGE PRIMARY KEY (product_image_id),
    CONSTRAINT fk_product_image_product FOREIGN KEY (product_id)
        REFERENCES product (product_id),
    CONSTRAINT ux_product_image_order UNIQUE (product_id, sort_order)
);
CREATE SEQUENCE seq_product_image START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE department_store_product
(
    department_store_product_id NUMBER               NOT NULL,
    department_store_id         NUMBER               NOT NULL,
    product_id                  NUMBER               NOT NULL,
    created_at                  DATE DEFAULT SYSDATE NOT NULL,
    updated_at                  DATE,
    CONSTRAINT PK_DEPARTMENT_STORE_PRODUCT PRIMARY KEY (department_store_product_id),
    CONSTRAINT fk_dsp_department_store FOREIGN KEY (department_store_id)
        REFERENCES department_store (department_store_id),
    CONSTRAINT fk_dsp_product FOREIGN KEY (product_id)
        REFERENCES product (product_id),
    CONSTRAINT ux_dsp_store_product UNIQUE (department_store_id, product_id)
);
CREATE SEQUENCE seq_department_store_product START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE review
(
    review_id  NUMBER               NOT NULL,
    star_rate  NUMBER               NOT NULL,
    content    VARCHAR2(255)        NOT NULL,
    member_id  NUMBER               NOT NULL,
    product_id NUMBER               NOT NULL,
    created_at DATE DEFAULT SYSDATE NOT NULL,
    updated_at DATE,
    CONSTRAINT PK_REVIEW PRIMARY KEY (review_id),
    CONSTRAINT ck_review_star_rate CHECK (star_rate BETWEEN 1 AND 5),
    CONSTRAINT fk_review_member FOREIGN KEY (member_id)
        REFERENCES member (member_id),
    CONSTRAINT fk_review_product FOREIGN KEY (product_id)
        REFERENCES product (product_id)
);
CREATE SEQUENCE seq_review START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


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


-- ------------------------- 포인트 -------------------------
CREATE TABLE point_exchange_history
(
    point_exchange_history_id NUMBER               NOT NULL,
    point_price               NUMBER               NOT NULL,
    status                    VARCHAR2(255)        NOT NULL,
    member_id                 NUMBER               NOT NULL,
    created_at                DATE DEFAULT SYSDATE NOT NULL,
    updated_at                DATE,
    CONSTRAINT PK_POINT_EXCHANGE_HISTORY PRIMARY KEY (point_exchange_history_id),
    CONSTRAINT ck_point_exchange_history_status CHECK (status IN ('ADD', 'USE')),
    CONSTRAINT fk_point_exchange_history_member FOREIGN KEY (member_id)
        REFERENCES member (member_id)
);
CREATE SEQUENCE seq_point_exchange_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- ------------------------- 고객 응대 -------------------------
CREATE TABLE customer_inquery
(
    customer_inquery_id NUMBER               NOT NULL,
    title               VARCHAR2(255)        NOT NULL,
    content             VARCHAR2(255)        NOT NULL,
    reply_status        VARCHAR2(255)        NOT NULL,
    reply               VARCHAR2(500)        NOT NULL,
    member_id           NUMBER               NOT NULL,
    created_at          DATE DEFAULT SYSDATE NOT NULL,
    updated_at          DATE,
    CONSTRAINT PK_CUSTOMER_INQUERY PRIMARY KEY (customer_inquery_id),
    CONSTRAINT ck_customer_inquery_reply_status CHECK (reply_status IN ('PENDING', 'ANSWERED')),
    CONSTRAINT fk_customer_inquery_member FOREIGN KEY (member_id)
        REFERENCES member (member_id)
);
CREATE SEQUENCE seq_customer_inquery START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- ------------------------- 래플 -------------------------
CREATE TABLE raffle
(
    raffle_id        NUMBER               NOT NULL,
    name             VARCHAR2(255)        NOT NULL,
    eco_stock_id     NUMBER               NOT NULL,
    eco_stock_amount NUMBER               NOT NULL,
    product_price    NUMBER               NOT NULL,
    start_date       DATE                 NOT NULL,
    end_date         DATE                 NOT NULL,
    description      VARCHAR2(2000)       NOT NULL,
    image_url        VARCHAR2(511)        NOT NULL,
    created_at       DATE DEFAULT SYSDATE NOT NULL,
    updated_at       DATE,
    CONSTRAINT PK_RAFFLE PRIMARY KEY (raffle_id),
    CONSTRAINT fk_raffle_eco_stock FOREIGN KEY (eco_stock_id)
        REFERENCES eco_stock (eco_stock_id)
);
CREATE SEQUENCE seq_raffle START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE raffle_history
(
    raffle_history_id NUMBER               NOT NULL,
    win_status        VARCHAR2(100),
    raffle_id         NUMBER               NOT NULL,
    member_id         NUMBER               NOT NULL,
    created_at        DATE DEFAULT SYSDATE NOT NULL,
    updated_at        DATE,
    CONSTRAINT PK_RAFFLE_HISTORY PRIMARY KEY (raffle_history_id),
    CONSTRAINT ck_raffle_history_win_status CHECK (win_status IN ('Y', 'N')),
    CONSTRAINT fk_raffle_history_raffle FOREIGN KEY (raffle_id)
        REFERENCES raffle (raffle_id),
    CONSTRAINT fk_raffle_history_member FOREIGN KEY (member_id)
        REFERENCES member (member_id)
);
CREATE SEQUENCE seq_raffle_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- --------------------------- 벡터 -----------------------------
CREATE TABLE product_vector
(
    product_vector_id NUMBER               NOT NULL,
    product_id        NUMBER               NOT NULL,
    created_at        DATE DEFAULT SYSDATE NOT NULL,
    updated_at        DATE,
    CONSTRAINT PK_PRODUCT_VECTOR PRIMARY KEY (product_vector_id),
    CONSTRAINT fk_product_vector_product FOREIGN KEY (product_id)
        REFERENCES product (product_id)
);
CREATE SEQUENCE seq_product_vector START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- --------------------------- 오프라인 결제 정보 -----------------------------
CREATE TABLE offline_pay_history
(
    offline_pay_history_id NUMBER PRIMARY KEY,
    price                  NUMBER               NOT NULL,
    paid_at                DATE                 NOT NULL,
    member_id              NUMBER               NOT NULL,
    created_at             DATE DEFAULT SYSDATE NOT NULL,

    CONSTRAINT fk_offline_pay_history_member FOREIGN KEY (member_id)
        REFERENCES member (member_id)
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

    CONSTRAINT fk_offline_prod_pay
        FOREIGN KEY (offline_pay_history_id) REFERENCES offline_pay_history (offline_pay_history_id)
);
CREATE SEQUENCE seq_offline_pay_product_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
