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
CREATE SEQUENCE seq_eco_stock START WITH 7 INCREMENT BY 1 NOCACHE NOCYCLE;

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (1, '제로컵(Zero Cup)', 100, 'https://storage.googleapis.com/planet_public_image/487.jpg',
        SYSDATE,
        NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (2, '애상(Eco 상품)', 200,
        'https://storage.googleapis.com/planet_public_image/green_leaf_recycle_sign.jpg', SYSDATE,
        NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (3, '이브이(EV)', 100, null, SYSDATE, NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (4, '제로백(Bag)', 100, null, SYSDATE, NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (5, '봉시활동', 1000, null, SYSDATE, NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (6, '기부온(Give_On)', 100, null, SYSDATE, NULL);

COMMIT;

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
    sell_count             number               not null,
    buy_count              number               not null,
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
