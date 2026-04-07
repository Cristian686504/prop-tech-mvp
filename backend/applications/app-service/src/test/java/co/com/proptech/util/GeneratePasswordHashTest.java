package co.com.proptech.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Test to generate BCrypt hash for test data
 * Run this test to get the correct hash for Test123! password
 */
class GeneratePasswordHashTest {
    
    @Test
    void generatePasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Test123!";
        String hash = encoder.encode(password);
        
        System.out.println("================================================================================");
        System.out.println("PASSWORD HASH FOR TEST DATA MIGRATION");
        System.out.println("================================================================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("================================================================================");
        System.out.println("IMPORTANT: Copy this hash to V5__seed_test_data.sql");
        System.out.println("Replace the current password_hash value with the hash above");
        System.out.println("================================================================================");
        
        // Verify
        boolean matches = encoder.matches(password, hash);
        System.out.println("\nVerification: " + (matches ? "✓ VALID" : "✗ INVALID"));
        
        // Also generate alternative format for direct SQL update
        System.out.println("\n--- SQL UPDATE STATEMENT ---");
        System.out.println("UPDATE users SET password_hash = '" + hash + "' WHERE email IN ('landlord@test.com', 'tenant@test.com');");
    }
}
