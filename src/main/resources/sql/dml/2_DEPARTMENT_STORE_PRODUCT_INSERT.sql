INSERT INTO department_store_product (department_store_product_id, department_store_id, product_id)
VALUES (1, 1, 1);

INSERT INTO department_store_product (department_store_product_id, department_store_id, product_id)
VALUES (2, 2, 1);

INSERT INTO department_store_product (department_store_product_id, department_store_id, product_id)
VALUES (3, 3, 1);

INSERT INTO department_store_product (department_store_product_id, department_store_id, product_id)
VALUES (4, 4, 1);

commit;

-- 현재 시퀀스 값 확인
SELECT SEQ_department_store_product.NEXTVAL
FROM dual;