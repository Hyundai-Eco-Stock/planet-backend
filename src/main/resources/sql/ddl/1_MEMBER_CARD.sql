-- auto-generated definition
create table MEMBER_CARD
(
    MEMBER_CARD_ID NUMBER               not null primary key,
    MEMBER_ID      NUMBER
        constraint FK_MEMBER_CARD_MEMBER references MEMBER,
    CARD_NUMBER    VARCHAR2(255) UNIQUE,
    CREATED_AT     DATE default SYSDATE not null,
    UPDATED_AT     DATE
);

CREATE SEQUENCE SEQ_MEMBER_CARD START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
