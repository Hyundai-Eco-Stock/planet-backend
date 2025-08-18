CREATE TABLE receipt_history
(
    receipt_history_id NUMBER               NOT NULL,
    receipt_no         VARCHAR2(511)        NOT NULL,
    member_id          NUMBER               NOT NULL,
    created_at         DATE DEFAULT SYSDATE NOT NULL,
    updated_at         DATE,
    CONSTRAINT PK_RECEIPT_HISTORY PRIMARY KEY (receipt_history_id),
    CONSTRAINT fk_receipt_history_member FOREIGN KEY (member_id)
        REFERENCES member (member_id)
);

CREATE SEQUENCE seq_receipt_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;