-- Create applications table
CREATE TABLE applications (
    id UUID PRIMARY KEY,
    property_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    evaluated_at TIMESTAMP,
    CONSTRAINT fk_applications_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_tenant FOREIGN KEY (tenant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_property_tenant_status UNIQUE (property_id, tenant_id, status)
);

-- Create indexes for faster lookups
CREATE INDEX idx_applications_tenant ON applications(tenant_id);
CREATE INDEX idx_applications_property ON applications(property_id);
CREATE INDEX idx_applications_status ON applications(status);
