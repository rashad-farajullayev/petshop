CREATE TABLE shopping_basket (
                                 id BINARY(36) NOT NULL PRIMARY KEY,
                                 created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 status ENUM('NEW', 'PAID', 'PROCESSED', 'UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',
                                 status_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 customer_id BINARY(16) NOT NULL,
                                 FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;