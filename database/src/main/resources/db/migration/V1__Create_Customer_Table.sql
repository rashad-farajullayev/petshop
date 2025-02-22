CREATE TABLE customer (
                          id BINARY(16) NOT NULL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          timezone VARCHAR(50) NOT NULL,
                          created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          owner VARCHAR(255) NOT NULL,
                          UNIQUE KEY uk_customer_name_owner (name, owner) -- Composite Unique Constraint
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
