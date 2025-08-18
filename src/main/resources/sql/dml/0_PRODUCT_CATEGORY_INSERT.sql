INSERT INTO product_category (category_id, name)
VALUES (1, '옷');

INSERT INTO product_category (category_id, name)
VALUES (2, '뷰티');

INSERT INTO product_category (category_id, name)
VALUES (3, '비누');

INSERT INTO product_category (category_id, name)
VALUES (4, '향수');

INSERT INTO product_category (category_id, name)
VALUES (5, '헤어');

INSERT INTO product_category (category_id, name)
VALUES (6, '식기류');

INSERT INTO product_category (category_id, name)
VALUES (7, '속옷');

commit;

-- 현재 시퀀스 값 확인
SELECT seq_product_category.NEXTVAL
FROM dual;

