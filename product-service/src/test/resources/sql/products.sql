DROP TABLE IF EXISTS products;

CREATE TABLE products
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(128)   NOT NULL UNIQUE,
    description VARCHAR(1024)  NOT NULL,
    price       NUMERIC(10, 2) NOT NULL,
    status      VARCHAR(32)    NOT NULL
);


INSERT INTO products(name, description, price, status)
VALUES ('Iphone 13', 'Best phone', 999.9, 'AVAILABLE'),
       ('Xiaomi 10', 'Middle phone', 399.9, 'OUT_OF_STOCK'),
       ('Samsung Galaxy S21', 'Flagship phone', 799.9, 'AVAILABLE'),
       ('Google Pixel 6', 'Great camera', 599.9, 'AVAILABLE'),
       ('OnePlus 9', 'Fast and smooth', 729.9, 'OUT_OF_STOCK'),
       ('Sony Xperia 5 III', 'Compact flagship', 849.9, 'AVAILABLE'),
       ('Motorola Edge 20', 'Affordable 5G phone', 499.9, 'AVAILABLE'),
       ('Huawei P40', 'No Google Services', 699.9, 'OUT_OF_STOCK'),
       ('Realme GT', 'Budget flagship', 429.9, 'AVAILABLE'),
       ('Asus ROG Phone 5', 'Gaming beast', 999.9, 'AVAILABLE');