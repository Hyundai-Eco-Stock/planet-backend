INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (1, '& other Stories', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (2, 'patagonia', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (3, 'BALMAIN', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (4, 'EKOBO', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (5, 'COS', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (6, 'ISOI', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (7, 'LUSH', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (8, 'CHANTECAILLE', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (9, 'ReXRe', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (10, 'LOMA', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (11, 'CLARINS', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (12, 'LE COUVEN', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (13, 'TOTEME', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (14, 'THE NORTH FACE', NULL, SYSDATE, NULL);

INSERT INTO brand (brand_id, name, image_url, created_at, updated_at)
VALUES (15, 'ROOTON', NULL, SYSDATE, NULL);

commit;

-- 현재 시퀀스 값 확인
SELECT SEQ_BRAND.NEXTVAL
FROM dual;
