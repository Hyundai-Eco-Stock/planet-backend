-- ------------------------------ 멤버 ------------------------------
CREATE TABLE member
(
    member_id      NUMBER                 NOT NULL,
    email          VARCHAR2(511)          NOT NULL,
    name           VARCHAR2(511)          NOT NULL,
    profile_url    VARCHAR2(1000),
    pwd_hash       VARCHAR2(1000),
    provider       VARCHAR2(255),
    birth          DATE,
    sex            VARCHAR2(100),
    address        VARCHAR2(100),
    detail_address VARCHAR2(100),
    zip_code       VARCHAR2(100),
    role           VARCHAR2(50),
    point          NUMBER(10,2) DEFAULT 0.00       NOT NULL,
    created_at     DATE   DEFAULT SYSDATE NOT NULL,
    updated_at     DATE,
    CONSTRAINT PK_MEMBER PRIMARY KEY (member_id),
    CONSTRAINT ck_member_sex CHECK (sex IN ('M', 'F')),
    CONSTRAINT ux_member_email UNIQUE (email)
);
CREATE SEQUENCE seq_member START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
