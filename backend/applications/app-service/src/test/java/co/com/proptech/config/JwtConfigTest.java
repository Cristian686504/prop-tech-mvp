package co.com.proptech.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtConfig Unit Tests")
class JwtConfigTest {

    private static final String VALID_SECRET = "a-valid-secret-key-that-is-at-least-32-chars!!";
    private static final long EXPIRATION = 86400000L;

    private JwtConfig configWith(String secret) {
        JwtConfig config = new JwtConfig();
        ReflectionTestUtils.setField(config, "jwtSecret", secret);
        ReflectionTestUtils.setField(config, "jwtExpiration", EXPIRATION);
        return config;
    }

    @Test
    @DisplayName("validateJwtSecret - should pass with a valid secret of 32+ chars")
    void shouldPassWithValidSecret() {
        assertDoesNotThrow(() -> configWith(VALID_SECRET).validateJwtSecret());
    }

    @Test
    @DisplayName("validateJwtSecret - should throw when secret is null")
    void shouldThrowWhenSecretIsNull() {
        JwtConfig config = new JwtConfig();
        ReflectionTestUtils.setField(config, "jwtSecret", null);
        ReflectionTestUtils.setField(config, "jwtExpiration", EXPIRATION);

        IllegalStateException ex = assertThrows(IllegalStateException.class, config::validateJwtSecret);
        assertTrue(ex.getMessage().contains("JWT_SECRET"));
    }

    @Test
    @DisplayName("validateJwtSecret - should throw when secret is blank")
    void shouldThrowWhenSecretIsBlank() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> configWith("   ").validateJwtSecret());
        assertTrue(ex.getMessage().contains("JWT_SECRET"));
    }

    @Test
    @DisplayName("validateJwtSecret - should throw when secret is shorter than 32 chars")
    void shouldThrowWhenSecretTooShort() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> configWith("short").validateJwtSecret());
        assertTrue(ex.getMessage().contains("32 characters"));
    }

    @Test
    @DisplayName("getJwtSecret - should return the configured secret")
    void shouldReturnConfiguredSecret() {
        JwtConfig config = configWith(VALID_SECRET);
        assertEquals(VALID_SECRET, config.getJwtSecret());
    }

    @Test
    @DisplayName("getJwtExpiration - should return the configured expiration")
    void shouldReturnConfiguredExpiration() {
        JwtConfig config = configWith(VALID_SECRET);
        assertEquals(EXPIRATION, config.getJwtExpiration());
    }
}
