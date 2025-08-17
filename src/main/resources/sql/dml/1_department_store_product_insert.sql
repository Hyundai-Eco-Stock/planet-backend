INSERT INTO department_store_product (department_store_product_id,
                                      department_store_id,
                                      product_id,
                                      created_at,
                                      updated_at)
VALUES (1,
        1,
        1,
        SYSDATE,
        NULL);

INSERT INTO department_store_product (department_store_product_id,
                                      department_store_id,
                                      product_id,
                                      created_at,
                                      updated_at)
VALUES (2,
        2,
        1,
        SYSDATE,
        NULL);

INSERT INTO department_store_product (department_store_product_id,
                                      department_store_id,
                                      product_id,
                                      created_at,
                                      updated_at)
VALUES (3,
        3,
        1,
        SYSDATE,
        NULL);

INSERT INTO department_store_product (department_store_product_id,
                                      department_store_id,
                                      product_id,
                                      created_at,
                                      updated_at)
VALUES (4,
        4,
        1,
        SYSDATE,
        NULL);

commit;

-- 현재 시퀀스 값 확인
SELECT SEQ_department_store_product.NEXTVAL
FROM dual;