package co.com.proptech.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for test data migration
 * Run this class to generate the hash for Test123! password
 * 
 * Usage: Run the main method and copy the output hash to V5__seed_test_data.sql
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "Test123!";
        String hash = encoder.encode(password);
        
        System.out.println("=".repeat(80));
        System.out.println("Password Hash Generator for Test Data");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println();
        System.out.println("Copy this hash to V5__seed_test_data.sql migration file");
        System.out.println("=".repeat(80));
        
        // Verify the hash works
        boolean matches = encoder.matches(password, hash);
        System.out.println("\nVerification: " + (matches ? "✓ Hash is valid" : "✗ Hash is invalid"));
    }
}
