-- Insert a test customer
INSERT INTO customer (id, name, timezone, created, owner)
VALUES (UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440000', '-', '')),
        'Test Customer',
        'Asia/Baku',
        NOW(),
        'admin');

-- Insert two shopping baskets for this customer
INSERT INTO shopping_basket (id, created, status, status_date, customer_id)
VALUES
    (UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), NOW(), 'NEW', NOW(),
     UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440000', '-', ''))),
    (UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440002', '-', '')), NOW(), 'PAID', NOW(),
     UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440000', '-', '')));

-- Insert items into the first shopping basket
INSERT INTO basket_items (id, description, amount, shopping_basket_id)
VALUES
    (UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440003', '-', '')), 'Laptop', 1,
     UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', ''))),
    (UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440004', '-', '')), 'Mouse', 2,
     UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')));

-- Insert items into the second shopping basket
INSERT INTO basket_items (id, description, amount, shopping_basket_id)
VALUES
    (UNHEX(REPLACE('AA0e8400-e29b-41d4-a716-446655440005', '-', '')), 'Keyboard', 1,
     UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440002', '-', ''))),
    (UNHEX(REPLACE('BB0e8400-e29b-41d4-a716-446655440006', '-', '')), 'Monitor', 1,
     UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440002', '-', ''))),
    (UNHEX(REPLACE('CC0e8400-e29b-41d4-a716-446655440007', '-', '')), 'Desk Chair', 1,
     UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440002', '-', '')));
