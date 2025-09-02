-- ----------------------- 상품/장바구니 (에코딜) -----------------------
CREATE TABLE brand
(
    brand_id   NUMBER               NOT NULL,
    name       VARCHAR2(255)        NOT NULL,
    image_url  VARCHAR2(511),
    created_at DATE DEFAULT SYSDATE NOT NULL,
    updated_at DATE,
    CONSTRAINT PK_BRAND PRIMARY KEY (brand_id)
);
DROP SEQUENCE seq_brand;
CREATE SEQUENCE seq_brand START WITH 16 INCREMENT BY 1 NOCACHE NOCYCLE;

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


-- 카테고리
CREATE TABLE product_category
(
    category_id NUMBER               NOT NULL,
    name        VARCHAR2(255)        NOT NULL,
    created_at  DATE DEFAULT SYSDATE NOT NULL,
    updated_at  DATE,
    CONSTRAINT PK_PRODUCT_CATEGORY PRIMARY KEY (category_id)
);
DROP SEQUENCE seq_product_category;
CREATE SEQUENCE seq_product_category START WITH 8 INCREMENT BY 1 NOCACHE NOCYCLE;

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



-- 상품
-- TODO: 향후 NOT NULL로 수정
-- TODO: check 추가 필요
CREATE TABLE product
(
    product_id      NUMBER                 NOT NULL,
    category_id     NUMBER                 NULL,
    brand_id        NUMBER                 NULL,
    name            VARCHAR2(255)          NULL,
    price           NUMBER                 NULL,
    image_url       VARCHAR2(1000)         NULL,
    quantity        NUMBER DEFAULT 99999   NOT NULL, -- 재고 수량
    eco_deal_status VARCHAR2(100),
    sale_percent    NUMBER,
    created_at      DATE   DEFAULT SYSDATE NOT NULL,
    updated_at      DATE,
    CONSTRAINT PK_PRODUCT PRIMARY KEY (product_id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id)
        REFERENCES product_category (category_id),
    CONSTRAINT fk_product_brand FOREIGN KEY (brand_id)
        REFERENCES brand (brand_id)
);
DROP SEQUENCE seq_product;
CREATE SEQUENCE seq_product START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


CREATE TABLE product_image
(
    product_image_id NUMBER               NOT NULL,
    product_id       NUMBER               NOT NULL,
    image_url        VARCHAR2(500)        NOT NULL,
    sort_order       NUMBER               NOT NULL, -- 1,2,3...
    alt_text         VARCHAR2(255),
    created_at       DATE DEFAULT SYSDATE NOT NULL,
    updated_at       DATE,
    CONSTRAINT PK_PRODUCT_IMAGE PRIMARY KEY (product_image_id),
    CONSTRAINT fk_product_image_product FOREIGN KEY (product_id)
        REFERENCES product (product_id),
    CONSTRAINT ux_product_image_order UNIQUE (product_id, sort_order)
);
DROP SEQUENCE seq_product_image;
CREATE SEQUENCE seq_product_image START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;


-- -------------------- 백화정 --------------------
CREATE TABLE department_store
(
    department_store_id NUMBER               NOT NULL,
    name                VARCHAR2(255)        NOT NULL,
    lat                 VARCHAR2(255)        NOT NULL,
    lng                 VARCHAR2(255)        NOT NULL,
    created_at          DATE DEFAULT SYSDATE NOT NULL,
    updated_at          DATE,
    CONSTRAINT PK_DEPARTMENT_STORE PRIMARY KEY (department_store_id)
);
DROP SEQUENCE seq_department_store;
CREATE SEQUENCE seq_department_store START WITH 5 INCREMENT BY 1 NOCACHE NOCYCLE;
INSERT INTO department_store (department_store_id, name, lat, lng, created_at, updated_at)
VALUES (1, '압구정 본점', 37.52738396911794, 127.02744273952618, SYSDATE, NULL);

INSERT INTO department_store (department_store_id, name, lat, lng, created_at, updated_at)
VALUES (2, '무역센터점', 37.50904326950084, 127.05997865750227, SYSDATE, NULL);

INSERT INTO department_store (department_store_id, name, lat, lng, created_at, updated_at)
VALUES (3, '동대문점', 37.52738396911794, 127.02744273952618, SYSDATE, NULL);

INSERT INTO department_store (department_store_id, name, lat, lng, created_at, updated_at)
VALUES (4, '신촌점', 37.52738396911794, 127.02744273952618, SYSDATE, NULL);

commit;


-- 백화정 상품
CREATE TABLE department_store_product
(
    department_store_product_id NUMBER               NOT NULL,
    department_store_id         NUMBER               NOT NULL,
    product_id                  NUMBER               NOT NULL,
    created_at                  DATE DEFAULT SYSDATE NOT NULL,
    updated_at                  DATE,
    CONSTRAINT PK_DEPARTMENT_STORE_PRODUCT PRIMARY KEY (department_store_product_id),
    CONSTRAINT fk_dsp_department_store FOREIGN KEY (department_store_id)
        REFERENCES department_store (department_store_id),
    CONSTRAINT fk_dsp_product FOREIGN KEY (product_id)
        REFERENCES product (product_id),
    CONSTRAINT ux_dsp_store_product UNIQUE (department_store_id, product_id)
);
DROP SEQUENCE seq_department_store_product;
CREATE SEQUENCE seq_department_store_product START WITH 5 INCREMENT BY 1 NOCACHE NOCYCLE;

INSERT INTO department_store_product (department_store_product_id, department_store_id, product_id)
VALUES (1, 1, 1);

INSERT INTO department_store_product (department_store_product_id, department_store_id, product_id)
VALUES (2, 2, 1);

INSERT INTO department_store_product (department_store_product_id, department_store_id, product_id)
VALUES (3, 3, 1);

INSERT INTO department_store_product (department_store_product_id, department_store_id, product_id)
VALUES (4, 4, 1);

commit;

-- CREATE TABLE review
-- (
--     review_id  NUMBER               NOT NULL,
--     star_rate  NUMBER               NOT NULL,
--     content    VARCHAR2(255)        NOT NULL,
--     member_id  NUMBER               NOT NULL,
--     product_id NUMBER               NOT NULL,
--     created_at DATE DEFAULT SYSDATE NOT NULL,
--     updated_at DATE,
--     CONSTRAINT PK_REVIEW PRIMARY KEY (review_id),
--     CONSTRAINT ck_review_star_rate CHECK (star_rate BETWEEN 1 AND 5),
--     CONSTRAINT fk_review_member FOREIGN KEY (member_id)
--         REFERENCES member (member_id),
--     CONSTRAINT fk_review_product FOREIGN KEY (product_id)
--         REFERENCES product (product_id)
-- );
-- CREATE SEQUENCE seq_review START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
