DROP TABLE IF EXISTS orders_products;
DROP TABLE IF EXISTS orders;

CREATE TABLE orders
(
    id         SERIAL PRIMARY KEY,
    user_id    UUID         NOT NULL,
    user_email VARCHAR(128) NOT NULL,
    order_date TIMESTAMP    NOT NULL,
    status     VARCHAR(32)  NOT NULL
);

CREATE TABLE orders_products
(
    id           SERIAL PRIMARY KEY,
    order_id     INT NOT NULL,
    product_id   INT NOT NULL,
    quantity     INT NOT NULL,
    inventory_id INT NOT NULL,
    CONSTRAINT fk_order_id FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT uk_order_product_inventory UNIQUE (order_id, product_id, inventory_id)
);


INSERT INTO orders (user_id, user_email, order_date, status)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'user1@example.com', '2025-02-17 10:00:00', 'PENDING'),
       ('550e8400-e29b-41d4-a716-446655440001', 'user2@example.com', '2025-02-17 11:00:00', 'CANCELED'),
       ('550e8400-e29b-41d4-a716-446655440002', 'user3@example.com', '2025-02-17 12:00:00', 'CONFIRMED'),
       ('550e8400-e29b-41d4-a716-446655440003', 'user4@example.com', '2025-02-17 13:00:00', 'PENDING'),
       ('550e8400-e29b-41d4-a716-446655440000', 'user1@example.com', '2025-02-17 14:00:00', 'CONFIRMED');

INSERT INTO orders_products (order_id, product_id, quantity, inventory_id)
VALUES (1, 1, 2, 1),
       (1, 3, 1, 2),
       (1, 5, 3, 1),
       (1, 7, 2, 2),
       (1, 9, 1, 1),

       (2, 2, 1, 2),
       (2, 4, 2, 1),
       (2, 6, 3, 2),
       (2, 8, 1, 1),
       (2, 10, 2, 2),

       (3, 1, 1, 1),
       (3, 2, 2, 2),
       (3, 3, 3, 1),
       (3, 4, 1, 2),
       (3, 5, 2, 1),

       (4, 6, 1, 2),
       (4, 7, 2, 1),
       (4, 8, 3, 2),

       (5, 9, 1, 1),
       (5, 10, 2, 2);
