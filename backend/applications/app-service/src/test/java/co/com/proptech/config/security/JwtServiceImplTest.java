package co.com.proptech.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtServiceImpl Unit Tests")
class JwtServiceImplTest {

    private static final String SECRET = "test-secret-key-that-is-at-least-32-chars-long!!";
    private static final long EXPIRATION_MS = 86400000L;

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(SECRET, EXPIRATION_MS);
    }

    @Test
    @DisplayName("generateToken - should produce a non-blank token")
    void shouldGenerateNonBlankToken() {
        String token = jwtService.generateToken(UUID.randomUUID(), "user@email.com", "LANDLORD");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("validateToken - should return true for a freshly generated token")
    void shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(UUID.randomUUID(), "user@email.com", "TENANT");
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    @DisplayName("validateToken - should return false for a gibberish token")
    void shouldReturnFalseForGarbageToken() {
        assertFalse(jwtService.validateToken("not.a.jwt"));
    }

    @Test
    @DisplayName("validateToken - should return false for a tampered token")
    void shouldReturnFalseForTamperedToken() {
        String token = jwtService.generateToken(UUID.randomUUID(), "user@email.com", "TENANT");
        String tampered = token.substring(0, token.length() - 6) + "XXXXXX";
        assertFalse(jwtService.validateToken(tampered));
    }

    @Test
    @DisplayName("extractUserId - should round-trip the UUID embedded in the token")
    void shouldExtractCorrectUserId() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "user@email.com", "LANDLORD");
        assertEquals(userId, jwtService.extractUserId(token));
    }

    @Test
    @DisplayName("extractEmail - should round-trip the email embedded in the token")
    void shouldExtractCorrectEmail() {
        String email = "landlord@proptech.com";
        String token = jwtService.generateToken(UUID.randomUUID(), email, "LANDLORD");
        assertEquals(email, jwtService.extractEmail(token));
    }
}
