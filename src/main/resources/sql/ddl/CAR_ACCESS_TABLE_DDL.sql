CREATE TABLE car_access_history
(
    car_access_history_id NUMBER PRIMARY KEY,
    car_number            VARCHAR2(255)        NOT NULL,
    status                VARCHAR2(20)         NOT NULL, -- ENTER / EXIT
    created_at            DATE DEFAULT SYSDATE NOT NULL
);
CREATE SEQUENCE seq_car_access_history START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;