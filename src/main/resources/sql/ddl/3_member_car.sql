DROP TABLE member_car;

CREATE TABLE member_car
(
    member_car_id NUMBER PRIMARY KEY,
    member_id     NUMBER               NOT NULL,
    car_number    VARCHAR2(255)        NOT NULL,
    created_at    DATE DEFAULT SYSDATE NOT NULL,
    updated_at    DATE,
    CONSTRAINT fk_member_car_member FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT ux_member_car_number UNIQUE (car_number)
);

CREATE SEQUENCE SEQ_MEMBER_CAR START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;