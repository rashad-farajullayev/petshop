CREATE TABLE items (
                       id BINARY(16) NOT NULL PRIMARY KEY,
                       description VARCHAR(255) NOT NULL,
                       amount INT NOT NULL,
                       shopping_basket_id BINARY(16) NOT NULL,
                       FOREIGN KEY (shopping_basket_id) REFERENCES shopping_basket(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;