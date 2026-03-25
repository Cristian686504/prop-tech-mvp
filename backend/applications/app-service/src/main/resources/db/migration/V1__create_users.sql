-- Create users table with VARCHAR enums and CHECK constraints
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    document_type VARCHAR(10) NOT NULL CHECK (document_type IN ('CC', 'CE', 'NIT', 'PP', 'TI')),
    document_id VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('LANDLORD', 'TENANT')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create index on document for faster lookups
CREATE INDEX idx_users_document ON users(document_type, document_id);
