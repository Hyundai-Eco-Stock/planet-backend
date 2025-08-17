INSERT INTO product (product_id,
                     category_id,
                     brand_id,
                     name,
                     image_url,
                     price,
                     quantity,
                     eco_deal_status,
                     sale_percent,
                     created_at,
                     updated_at)
VALUES (1,
        NULL,
        NULL,
        '엄마 도시락',
        null,
        13000,
        24,
        'Y',
        25,
        SYSDATE,
        NULL);

commit;

-- 현재 시퀀스 값 확인
SELECT seq_product.NEXTVAL
FROM dual;

