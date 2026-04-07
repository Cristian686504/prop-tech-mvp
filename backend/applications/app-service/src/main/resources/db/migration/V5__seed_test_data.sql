-- Migration V5: Seed test data for QA environment
-- This migration inserts test users and properties as specified in TEST_PLAN.md section 6.2
-- WARNING: Only run in test/staging environments, NOT in production

-- Insert test landlord user
-- Email: landlord@test.com
-- Password: Test123! (same for all test users for simplicity)
-- BCrypt hash generated with cost factor 10
INSERT INTO users (id, name, email, password_hash, phone, document_type, document_id, role, created_at, monthly_income, credit_score)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Juan Pérez',
    'landlord@test.com',
    '$2a$10$K6etRva0G/5Pzmzf.ZHLc.0NJkPSVaLVw7h6mSq6LTDgB/aj3TLsq',
    '+573001234567',
    'CC',
    '1234567890',
    'LANDLORD',
    CURRENT_TIMESTAMP,
    NULL,
    NULL
);

-- Insert test tenant user with financial data
-- Email: tenant@test.com
-- Password: Test123! (same for all test users for simplicity)
-- BCrypt hash generated with cost factor 10
-- Financial profile: Medium risk (score 650, income 4,000,000 COP)
INSERT INTO users (id, name, email, password_hash, phone, document_type, document_id, role, created_at, monthly_income, credit_score)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'María Rodríguez',
    'tenant@test.com',
    '$2a$10$K6etRva0G/5Pzmzf.ZHLc.0NJkPSVaLVw7h6mSq6LTDgB/aj3TLsq',
    '+573007654321',
    'CC',
    '9876543210',
    'TENANT',
    CURRENT_TIMESTAMP,
    4000000.00,
    650
);

-- Insert test property 1: Apartamento en Chapinero
INSERT INTO properties (id, title, description, address, price, status, landlord_id, created_at, updated_at)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'Apartamento Moderno en Chapinero',
    'Hermoso apartamento de 2 habitaciones y 2 baños, completamente amoblado. Ubicado en el corazón de Chapinero, cerca de restaurantes, transporte público y zonas comerciales. Incluye parqueadero y cuarto útil. Edificio con portería 24/7, gimnasio y salón social.',
    'Calle 63 #7-30, Chapinero, Bogotá',
    2500000.00,
    'AVAILABLE',
    '11111111-1111-1111-1111-111111111111',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert images for property 1
INSERT INTO property_images (property_id, image_url)
VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688');

-- Insert test property 2: Casa en Usaquén
INSERT INTO properties (id, title, description, address, price, status, landlord_id, created_at, updated_at)
VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'Casa Familiar en Usaquén',
    'Amplia casa de 3 pisos con 4 habitaciones, 3 baños, sala comedor, cocina integral, zona de lavandería y patio trasero. Ideal para familias. Ubicada en barrio tranquilo y seguro de Usaquén. Cerca de colegios, supermercados y parques. Garaje para 2 vehículos.',
    'Carrera 7 #145-28, Usaquén, Bogotá',
    3800000.00,
    'AVAILABLE',
    '11111111-1111-1111-1111-111111111111',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert images for property 2
INSERT INTO property_images (property_id, image_url)
VALUES 
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'https://images.unsplash.com/photo-1568605114967-8130f3a36994'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c');

-- Insert test property 3: Studio en El Poblado
INSERT INTO properties (id, title, description, address, price, status, landlord_id, created_at, updated_at)
VALUES (
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'Studio Ejecutivo en El Poblado',
    'Acogedor studio perfecto para profesionales o estudiantes. Espacio abierto con área de cocina integrada, baño completo y closet. Edificio moderno con excelente ubicación en El Poblado, Medellín. A pasos de la estación del metro, centros comerciales y vida nocturna. Incluye servicios públicos.',
    'Carrera 43A #10-50, El Poblado, Medellín',
    1800000.00,
    'AVAILABLE',
    '11111111-1111-1111-1111-111111111111',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert images for property 3
INSERT INTO property_images (property_id, image_url)
VALUES 
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'https://images.unsplash.com/photo-1536376072261-38c75010e6c9'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'https://images.unsplash.com/photo-1554995207-c18c203602cb'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf');

-- Add helpful comment for QA team
COMMENT ON TABLE users IS 'Test users: landlord@test.com and tenant@test.com - Both use password: Test123!';
