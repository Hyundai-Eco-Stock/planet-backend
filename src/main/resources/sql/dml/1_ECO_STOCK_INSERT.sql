INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (1, '제로컵(Zero Cup)', 100, 'https://storage.googleapis.com/planet_public_image/487.jpg',
        SYSDATE,
        NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (2, '애상(Eco 상품)', 200,
        'https://storage.googleapis.com/planet_public_image/green_leaf_recycle_sign.jpg', SYSDATE,
        NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (3, '이브이(EV)', 100, null, SYSDATE, NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (4, '제로백(Bag)', 100, null, SYSDATE, NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (5, '봉시활동', 1000, null, SYSDATE, NULL);

INSERT INTO eco_stock (eco_stock_id, name, quantity, image_url, created_at, updated_at)
VALUES (6, '기부온(Give_On)', 100, null, SYSDATE, NULL);

COMMIT;