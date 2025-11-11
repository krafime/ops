-- Create UUID extension for PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Master Data Seed Script
-- System Admin UUID: 792b3990-9315-405a-ba5a-da07770c1edf

-- ROLE TYPES
INSERT INTO role_types (id, role_code, role_name, is_active, created_at, created_by, opt_lock)
VALUES
    (uuid_generate_v4(), 'SA', 'Super Admin', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'GTW', 'User Gateway', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    ('792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 'SYS', 'System Admin', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'CUST', 'Customer', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0);

-- PAYMENT STATUSES
INSERT INTO payment_statuses (id, status_code, status_name, is_active, created_at, created_by, opt_lock)
VALUES
    (uuid_generate_v4(), 'PROCESSING', 'Sedang Diproses', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'SUCCESS', 'Berhasil', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'FAILED', 'Gagal', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'CANCELLED', 'Dibatalkan', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0);

-- PAYMENT TYPES
INSERT INTO payment_types (id, payment_code, payment_name, payment_fee, is_active, created_at, created_by, opt_lock)
VALUES
    (uuid_generate_v4(), 'QRIS', 'QRIS', 500.00, true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'VIRTUAL_ACCOUNT', 'Virtual Account', 2500.00, true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'SHOPPE_PAY', 'Shoppe Pay', 1000.00, true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'OVO', 'OVO', 1000.00, true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'DANA', 'DANA', 1000.00, true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'GOPAY', 'Gopay', 1000.00, true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0);

-- PRODUCT TYPES
INSERT INTO product_types (id, product_code, product_name, is_active, created_at, created_by, opt_lock)
VALUES
    (uuid_generate_v4(), 'PLN', 'PLN', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'PULSA', 'Pulsa', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'INTERNET', 'Internet', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'BPJS', 'BPJS', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0),
    (uuid_generate_v4(), 'PDAM', 'PDAM', true, NOW(), '792b3990-9315-405a-ba5a-da07770c1edf'::uuid, 0);

-- USER GATEWAY (using gateway.uuid from application.properties)
INSERT INTO users (id, email, password, full_name, role_types_id, is_active, created_at, created_by, opt_lock)
VALUES (
    '98e06d47-ce74-4d8c-8811-fe754b97cd70'::uuid,
    'krafime+3@gmail.com',
    '$2a$12$BPbXTA2qgaFFCZlWInrYyODjuc.PX2XUBTMyWwbr/B8tG2/wvVSny',
    'User Gateway',
    (SELECT id FROM role_types WHERE role_code = 'GTW' LIMIT 1),
    true,
    NOW(),
    '792b3990-9315-405a-ba5a-da07770c1edf'::uuid,
    0
),
(
'792b3990-9315-405a-ba5a-da07770c1edf'::uuid,)
    'krafime+1@gmail.com',
    '$2a$12$BPbXTA2qgaFFCZlWInrYyODjuc.PX2XUBTMyWwbr/B8tG2/wvVSny',
    'System Admin',
    (SELECT id FROM role_types WHERE role_code = 'SYS' LIMIT 1),
    true,
    NOW(),
    '792b3990-9315-405a-ba5a-da07770c1edf'::uuid,
    0
),
(
'b481889d-b67e-4caf-9dd8-f58625fcdc19'::uuid,
    'krafime+2@gmail.com',
    '$2a$12$BPbXTA2qgaFFCZlWInrYyODjuc.PX2XUBTMyWwbr/B8tG2/wvVSny',
    'Super Admin',
    (SELECT id FROM role_types WHERE role_code = 'SA' LIMIT 1),
    true,
    NOW(),
    '792b3990-9315-405a-ba5a-da07770c1edf'::uuid,
    0
    ),
    (
    '01462d68-7b51-4f18-be09-013395c5fce5'::uuid,
    'krafime+10@gmail.com',
    '$2a$12$BPbXTA2qgaFFCZlWInrYyODjuc.PX2XUBTMyWwbr/B8tG2/wvVSny',
    'Customer User',
    (SELECT id FROM role_types WHERE role_code = 'CUST' LIMIT 1),
    true,
    NOW(),
    '792b3990-9315-405a-ba5a-da07770c1edf'::uuid,
    0
    )
;

