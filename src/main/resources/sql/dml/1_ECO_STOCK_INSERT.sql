INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (1, '텀블러', 100, 'https://storage.googleapis.com/planet_public_image/487.jpg', SYSDATE,
        NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (2, '친환경 제품', 200,
        'https://storage.googleapis.com/planet_public_image/green_leaf_recycle_sign.jpg', SYSDATE,
        NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (3, '전기차', 100, null, SYSDATE, NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (4, '종이백 미사용', 100, null, SYSDATE, NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (5, '봉시활동', 1000, null, SYSDATE, NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (6, '기부', 100, null, SYSDATE, NULL);

COMMIT;