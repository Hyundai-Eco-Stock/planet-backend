INSERT INTO department_store (department_store_id, name, lat, lng, created_at, updated_at)
VALUES (1, '압구정 본점', 37.52738396911794, 127.02744273952618, SYSDATE, NULL);

INSERT INTO department_store (department_store_id, name, lat, lng, created_at, updated_at)
VALUES (2, '무역센터점', 37.50904326950084, 127.05997865750227, SYSDATE, NULL);

INSERT INTO department_store (department_store_id, name, lat, lng, created_at, updated_at)
VALUES (3, '동대문점', 37.52738396911794, 127.02744273952618, SYSDATE, NULL);

INSERT INTO department_store (department_store_id, name, lat, lng, created_at, updated_at)
VALUES (4, '신촌점', 37.52738396911794, 127.02744273952618, SYSDATE, NULL);

commit;

-- 현재 시퀀스 값 확인
SELECT SEQ_DEPARTMENT_STORE.NEXTVAL
FROM dual;

