-- Migration V4: Add financial profile fields to users table
-- These fields are auto-generated for TENANT users only
-- Used for landlord risk evaluation (HU007)

ALTER TABLE users
    ADD COLUMN monthly_income DECIMAL(19, 2),
    ADD COLUMN credit_score INTEGER;

-- Add constraint for valid credit score range (300-850 is standard range)
ALTER TABLE users
    ADD CONSTRAINT check_credit_score_range 
    CHECK (credit_score IS NULL OR (credit_score >= 300 AND credit_score <= 850));

-- Add index for potential future queries filtering by credit score
CREATE INDEX idx_users_credit_score ON users(credit_score);

-- Add comment to document the purpose
COMMENT ON COLUMN users.monthly_income IS 'Simulated monthly income in COP for TENANT users - auto-generated at registration';
COMMENT ON COLUMN users.credit_score IS 'Simulated credit score (300-850) for TENANT users - auto-generated at registration';
