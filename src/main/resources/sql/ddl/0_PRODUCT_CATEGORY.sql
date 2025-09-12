-- auto-generated definition
create table PRODUCT_CATEGORY
(
    CATEGORY_ID NUMBER               not null
        constraint PK_PRODUCT_CATEGORY
            primary key,
    NAME        VARCHAR2(255)        not null,
    CREATED_AT  DATE default SYSDATE not null,
    UPDATED_AT  DATE,
    IMAGE_URL   VARCHAR2(1024 char),
    SORT_ORDER  NUMBER
)
/

