INSERT INTO customer (id, name, timezone, created, owner) VALUES
(X'c3f1a7b6a8f24b9aa75b8e657c14d3b2', 'Global Pet Supplies', 'UTC', NOW(), 'admin-secret-token-123456'),
(X'91ab4d34ef2046b2b9a80cf5fef9f83a', 'Tenant1 Cust1', 'America/New_York', NOW(), 'tenant1-secret-token-abcdef'),
(X'52e88c2f9c9648e784dc6c4c38dff25f', 'Tenant1 Cust2', 'America/New_York', NOW(), 'tenant1-secret-token-abcdef'),
(X'd2a1e389ba9d40e0b5d66fd01d9e09d7', 'Tenant2 Cust1', 'Asia/Tokyo', NOW(), 'tenant2-secret-token-ghijkl'),
(X'7acaf91db9b54135a21fd8cbe78e4f9c', 'Tenant2 Cust2', 'Asia/Tokyo', NOW(), 'tenant2-secret-token-ghijkl'),
(X'fe1eaa063db547bcb078bbadf89f33c6', 'Tenant2 Cust3', 'Asia/Tokyo', NOW(), 'tenant2-secret-token-ghijkl');

INSERT INTO shopping_basket (id, created, status, status_date, customer_id) VALUES
(X'f7d4c6b9dfe94c478e4dd60cf9f6a6a1', NOW(), 'NEW', NOW(), X'91ab4d34ef2046b2b9a80cf5fef9f83a'),
(X'bc6d36e938574f36a4d862b88b8ebfcb', NOW(), 'PAID', NOW(), X'91ab4d34ef2046b2b9a80cf5fef9f83a'),
(X'e98fbd28c6d84369a5e14b52c624f1b3', NOW(), 'PROCESSED', NOW(), X'91ab4d34ef2046b2b9a80cf5fef9f83a'),
(X'13a9dfc1c2b341e78a16a8e1f4952eb9', NOW(), 'UNKNOWN', NOW(), X'52e88c2f9c9648e784dc6c4c38dff25f'),
(X'85b7cd8fd729490ca2a2de192be92ff7', NOW(), 'NEW', NOW(), X'52e88c2f9c9648e784dc6c4c38dff25f'),
(X'ef492dff4cf143e59eaa512c4cf0a9bd', NOW(), 'PAID', NOW(), X'd2a1e389ba9d40e0b5d66fd01d9e09d7'),
(X'8fae5c90d4df45f4b2ee7687e874c8fc', NOW(), 'PROCESSED', NOW(), X'7acaf91db9b54135a21fd8cbe78e4f9c'),
(X'c2d3f891a1f04c5d8922f4e4b7b8c9df', NOW(), 'UNKNOWN', NOW(), X'7acaf91db9b54135a21fd8cbe78e4f9c');

INSERT INTO basket_items (id, description, amount, shopping_basket_id) VALUES
(X'ee6a5f8db37f4ddaa2dc68c3b1f1e6fb', 'Premium Dog Food', 2, X'f7d4c6b9dfe94c478e4dd60cf9f6a6a1'),
(X'caa7f6d98ecb4fc89e0e2e8bb4e4dc7d', 'Cat Scratching Post', 1, X'f7d4c6b9dfe94c478e4dd60cf9f6a6a1'),
(X'caf2ab1e1fcb4e72b7564c385ecf6e99', 'Organic Bird Seeds', 3, X'bc6d36e938574f36a4d862b88b8ebfcb'),
(X'bea6fa0b3ad748c2816cbb58e4c44f71', 'Aquarium Water Filter', 1, X'e98fbd28c6d84369a5e14b52c624f1b3'),
(X'84b3b4e9a90d44e8b6fc2babe7625f8d', 'Reptile Heat Lamp', 1, X'13a9dfc1c2b341e78a16a8e1f4952eb9'),
(X'1cd82e90fb2944eaa2e80e1a1f68b7d5', 'Hamster Running Wheel', 2, X'8fae5c90d4df45f4b2ee7687e874c8fc'),
(X'fa3d8e1e51d44a0e8aa5fbd82e491bcf', 'Parrot Cage', 1, X'c2d3f891a1f04c5d8922f4e4b7b8c9df');

INSERT INTO basket_items (id, description, amount, shopping_basket_id) VALUES
(X'51b2f90cb6f448d3a16a37f02e4d8e90', 'Dog Chew Toys', 3, X'bc6d36e938574f36a4d862b88b8ebfcb'),
(X'92b4d18cf7a34e0db06cda123bfa5d6e', 'Automatic Pet Feeder', 2, X'85b7cd8fd729490ca2a2de192be92ff7'),
(X'7ea0d34b1f9249aab62a3f4e24fa5dcd', 'Pet Grooming Kit', 1, X'ef492dff4cf143e59eaa512c4cf0a9bd'),
(X'df81c49b72e84b9484e7bfa6d3e0b5ca', 'Rabbit Hay Feeder', 5, X'8fae5c90d4df45f4b2ee7687e874c8fc'),
(X'ea5b27c68d9b4b67a4b6f8325db4c1fa', 'Tropical Fish Flakes', 4, X'c2d3f891a1f04c5d8922f4e4b7b8c9df'),
(X'31cf5ba84d9f4fb8a01b6825ca57e0da', 'Snake Terrarium Kit', 2, X'13a9dfc1c2b341e78a16a8e1f4952eb9'),
(X'bd5f62d19b374ad88e4b53c1fa90b5ea', 'Bird Perch Stand', 3, X'e98fbd28c6d84369a5e14b52c624f1b3'),
(X'cb67bfa481e64563a56a9e0b5dca4f92', 'Pet Travel Carrier', 2, X'bc6d36e938574f36a4d862b88b8ebfcb');
