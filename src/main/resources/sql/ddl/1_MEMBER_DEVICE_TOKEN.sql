CREATE TABLE member_device_token
(
    id         NUMBER PRIMARY KEY,
    member_id  NUMBER               NOT NULL,
    fcm_token  VARCHAR2(1000)       NOT NULL,
    created_at DATE DEFAULT SYSDATE NOT NULL,
    updated_at DATE,
    CONSTRAINT fk_member_device FOREIGN KEY (member_id)
        REFERENCES member (member_id)
);

CREATE SEQUENCE SEQ_MEMBER_DEVICE_TOKEN START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;