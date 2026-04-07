package co.com.proptech.model.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Exceptions Unit Tests")
class DomainExceptionsTest {

    // ---- InvalidCredentialsException ----

    @Test
    @DisplayName("InvalidCredentialsException - should store message")
    void invalidCredentialsShouldStoreMessage() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid credentials");
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    @DisplayName("InvalidCredentialsException - should be RuntimeException subtype")
    void invalidCredentialsShouldBeRuntimeException() {
        assertInstanceOf(RuntimeException.class, new InvalidCredentialsException("err"));
    }

    // ---- UnauthorizedOperationException ----

    @Test
    @DisplayName("UnauthorizedOperationException - should store message")
    void unauthorizedShouldStoreMessage() {
        UnauthorizedOperationException ex = new UnauthorizedOperationException("Forbidden");
        assertEquals("Forbidden", ex.getMessage());
    }

    @Test
    @DisplayName("UnauthorizedOperationException - should store message and cause")
    void unauthorizedShouldStoreMessageAndCause() {
        Throwable cause = new IllegalStateException("root");
        UnauthorizedOperationException ex = new UnauthorizedOperationException("Forbidden", cause);
        assertEquals("Forbidden", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    @DisplayName("UnauthorizedOperationException - should be RuntimeException subtype")
    void unauthorizedShouldBeRuntimeException() {
        assertInstanceOf(RuntimeException.class, new UnauthorizedOperationException("err"));
    }
}
