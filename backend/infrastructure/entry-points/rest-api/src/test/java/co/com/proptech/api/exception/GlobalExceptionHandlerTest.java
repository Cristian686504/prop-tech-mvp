package co.com.proptech.api.exception;

import co.com.proptech.model.exceptions.UnauthorizedOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler.
 * Validates that proper HTTP status codes are returned for different exception types.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 403 Forbidden when AccessDeniedException is thrown")
    void shouldReturn403ForAccessDenied() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("User lacks LANDLORD role");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleAccessDenied(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Access denied. You do not have permission to perform this action.");
    }

    @Test
    @DisplayName("Should return 403 Forbidden when UnauthorizedOperationException is thrown")
    void shouldReturn403ForUnauthorizedOperation() {
        // Given
        UnauthorizedOperationException exception = new UnauthorizedOperationException("Only landlords can publish properties");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleUnauthorizedOperation(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Only landlords can publish properties");
    }

    @Test
    @DisplayName("Should return 400 Bad Request when HttpMessageNotReadableException is thrown")
    void shouldReturn400ForHttpMessageNotReadable() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("JSON parse error", (Throwable) null);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleHttpMessageNotReadable(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should return 400 Bad Request when IllegalArgumentException is thrown")
    void shouldReturn400ForIllegalArgument() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalArgument(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid argument");
    }

    @Test
    @DisplayName("Should return 400 Bad Request when IllegalStateException is thrown")
    void shouldReturn400ForIllegalState() {
        // Given
        IllegalStateException exception = new IllegalStateException("Only landlords can publish properties");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalState(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Only landlords can publish properties");
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error for generic exceptions")
    void shouldReturn500ForGenericException() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGenericException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
    }

    @Test
    @DisplayName("AccessDeniedException should NOT return 500 (regression test for bug fix)")
    void accessDeniedShouldNotReturn500() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Insufficient privileges");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleAccessDenied(exception);

        // Then - This is the critical assertion: 403, NOT 500
        assertThat(response.getStatusCode())
                .as("AccessDeniedException must return 403 Forbidden, not 500 Internal Server Error")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("UnauthorizedOperationException should return 403 (business authorization)")
    void unauthorizedOperationShouldReturn403() {
        // Given
        UnauthorizedOperationException exception = new UnauthorizedOperationException("Only landlords can publish properties");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleUnauthorizedOperation(exception);

        // Then - Business authorization errors should return 403
        assertThat(response.getStatusCode())
                .as("UnauthorizedOperationException must return 403 Forbidden for business authorization rules")
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("Only landlords can publish properties");
    }

    @Test
    @DisplayName("IllegalStateException should NOT return 500 (regression test for bug fix)")
    void illegalStateShouldNotReturn500() {
        // Given
        IllegalStateException exception = new IllegalStateException("Only landlords can publish properties");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalState(exception);

        // Then - This is the critical assertion: 400, NOT 500
        assertThat(response.getStatusCode())
                .as("IllegalStateException must return 400 Bad Request, not 500 Internal Server Error")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
