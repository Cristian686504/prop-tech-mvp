-- Create user_role enum
CREATE TYPE user_role AS ENUM ('LANDLORD', 'TENANT');

-- Create document_type enum
CREATE TYPE document_type AS ENUM ('CC', 'CE', 'NIT', 'PP', 'TI');

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    document_type document_type NOT NULL,
    document_id VARCHAR(50) NOT NULL,
    role user_role NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create index on document for faster lookups
CREATE INDEX idx_users_document ON users(document_type, document_id);
