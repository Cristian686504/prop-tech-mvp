-- Reset script for test database
-- This script cleans all data from tables while preserving the schema
-- Run this before each test cycle to ensure a clean state

-- Disable foreign key checks temporarily
SET session_replication_role = 'replica';

-- Clear all application data (in reverse order of dependencies)
TRUNCATE TABLE property_images CASCADE;
TRUNCATE TABLE applications CASCADE;
TRUNCATE TABLE properties CASCADE;
TRUNCATE TABLE users CASCADE;

-- Re-enable foreign key checks
SET session_replication_role = 'origin';

-- Reset sequences if any
-- (Currently using UUIDs, so no sequences to reset)

-- Verify clean state
SELECT 'users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'properties', COUNT(*) FROM properties
UNION ALL
SELECT 'property_images', COUNT(*) FROM property_images
UNION ALL
SELECT 'applications', COUNT(*) FROM applications;

-- IMPORTANT: After running this script, the database is empty
-- To reload test data, you have two options:
-- 1. Run: \i backend/applications/app-service/src/main/resources/db/migration/V5__seed_test_data.sql
-- 2. Or use: docker-compose down -v && docker-compose up -d (recreates everything)
