-- Create properties table
CREATE TABLE properties (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    address VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL CHECK (price > 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'RENTED', 'INACTIVE')),
    landlord_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_properties_landlord FOREIGN KEY (landlord_id) REFERENCES users(id)
);

-- Create property_images table for the @ElementCollection mapping
CREATE TABLE property_images (
    property_id UUID NOT NULL,
    image_url VARCHAR(500),
    CONSTRAINT fk_property_images_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

-- Create indexes for faster lookups
CREATE INDEX idx_properties_landlord ON properties(landlord_id);
CREATE INDEX idx_properties_status ON properties(status);
CREATE INDEX idx_property_images_property ON property_images(property_id);
