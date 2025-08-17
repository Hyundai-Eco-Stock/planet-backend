INSERT INTO product_category (category_id, name, created_at, updated_at)
VALUES (1, '옷', SYSDATE, NULL);

INSERT INTO product_category (category_id, name, created_at, updated_at)
VALUES (2, '뷰티', SYSDATE, NULL);

INSERT INTO product_category (category_id, name, created_at, updated_at)
VALUES (3, '비누', SYSDATE, NULL);

INSERT INTO product_category (category_id, name, created_at, updated_at)
VALUES (4, '향수', SYSDATE, NULL);

INSERT INTO product_category (category_id, name, created_at, updated_at)
VALUES (5, '헤어', SYSDATE, NULL);

INSERT INTO product_category (category_id, name, created_at, updated_at)
VALUES (6, '식기류', SYSDATE, NULL);

INSERT INTO product_category (category_id, name, created_at, updated_at)
VALUES (7, '속옷', SYSDATE, NULL);

commit;

-- 현재 시퀀스 값 확인
SELECT seq_product_category.NEXTVAL
FROM dual;

