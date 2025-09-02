-- ------------------------- 래플 -------------------------
CREATE TABLE RAFFLE
(
    RAFFLE_ID        NUMBER               NOT NULL
        CONSTRAINT PK_RAFFLE PRIMARY KEY,
    ECO_STOCK_ID     NUMBER               NOT NULL,
    PRODUCT_ID       NUMBER               NOT NULL,
    ECO_STOCK_AMOUNT NUMBER               NOT NULL,
    START_DATE       DATE                 NOT NULL,
    END_DATE         DATE                 NOT NULL,
    CREATED_AT       DATE DEFAULT SYSDATE NOT NULL,
    UPDATED_AT       DATE,

    CONSTRAINT FK_RAFFLE_ECO_STOCK
        FOREIGN KEY (ECO_STOCK_ID)
            REFERENCES ECO_STOCK (ECO_STOCK_ID),

    CONSTRAINT FK_RAFFLE_PRODUCT
        FOREIGN KEY (PRODUCT_ID)
            REFERENCES PRODUCT (PRODUCT_ID)
);

CREATE SEQUENCE seq_raffle START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE raffle_history
(
    raffle_history_id NUMBER                        NOT NULL,
    win_status        VARCHAR2(100) default 'N',
    raffle_id         NUMBER                        NOT NULL,
    member_id         NUMBER                        NOT NULL,
    created_at        DATE          DEFAULT SYSDATE NOT NULL,
    updated_at        DATE,
    CONSTRAINT PK_RAFFLE_HISTORY PRIMARY KEY (raffle_history_id),
    CONSTRAINT ck_raffle_history_win_status CHECK (win_status IN ('Y', 'N')),
    CONSTRAINT fk_raffle_history_raffle FOREIGN KEY (raffle_id)
        REFERENCES raffle (raffle_id),
    CONSTRAINT fk_raffle_history_member FOREIGN KEY (member_id)
        REFERENCES member (member_id)
);
CREATE SEQUENCE seq_raffle_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
