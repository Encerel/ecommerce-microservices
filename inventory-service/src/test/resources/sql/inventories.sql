DROP TABLE IF EXISTS inventories_products;
DROP TABLE IF EXISTS inventories;


CREATE TABLE inventories
(
    id       SERIAL PRIMARY KEY,
    location VARCHAR(128) NOT NULL
);

CREATE TABLE inventories_products
(
    id           SERIAL PRIMARY KEY,
    inventory_id INT,
    product_id   INT NOT NULL,
    stock        INT NOT NULL,
    CONSTRAINT fk_inventory_id FOREIGN KEY (inventory_id) REFERENCES inventories (id)
);

INSERT INTO inventories (location)
VALUES ('Belarus');

INSERT INTO inventories_products (inventory_id, product_id, stock)
VALUES (1, 1, 14),
       (1, 2, 24),
       (1, 3, 4),
       (1, 4, 0),
       (1, 5, 43),
       (1, 6, 1),
       (1, 7, 0),
       (1, 8, 5),
       (1, 9, 81),
       (1, 10, 0);

