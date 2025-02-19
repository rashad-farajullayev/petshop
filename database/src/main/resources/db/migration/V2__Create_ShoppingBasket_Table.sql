CREATE TABLE shopping_basket (
                                 id BINARY(16) NOT NULL PRIMARY KEY,
                                 created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 status VARCHAR(50) NOT NULL,
                                 status_date TIMESTAMP NOT NULL,
                                 customer_id BINARY(16) NOT NULL,
                                 CONSTRAINT fk_shopping_basket_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
