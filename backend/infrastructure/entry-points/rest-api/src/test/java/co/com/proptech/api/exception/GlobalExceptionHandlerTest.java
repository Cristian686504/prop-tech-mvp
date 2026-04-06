package co.com.proptech.api.exception;

import co.com.proptech.model.exceptions.InvalidCredentialsException;
import co.com.proptech.model.exceptions.UnauthorizedOperationException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler.
 * Validates that proper HTTP status codes are returned for different exception types.
 */
@ExtendWith(MockitoExtension.class)
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
    @DisplayName("Should extract field name from Jackson deserialization error")
    void shouldExtractFieldNameFromJacksonError() {
        // Given - Simulate real Jackson error message for invalid price field
        String jacksonErrorMessage = 
            "Cannot deserialize value of type `java.math.BigDecimal` from String \"not-a-number\": " +
            "not a valid representation (through reference chain: co.com.proptech.api.dto.PublishPropertyRequestDto[\"price\"])";
        
        InvalidFormatException jacksonCause = new InvalidFormatException(
            null, jacksonErrorMessage, "not-a-number", BigDecimal.class);
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
            "JSON parse error: " + jacksonErrorMessage, jacksonCause);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleHttpMessageNotReadable(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
            .contains("price")
            .contains("numeric value");
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
    @DisplayName("MaxUploadSizeExceededException should return 413 (not 500)")
    void maxUploadSizeExceededShouldReturn413() {
        // Given
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(250L * 1024 * 1024);

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleMaxUploadSizeExceeded(exception);

        // Then
        assertThat(response.getStatusCode())
                .as("MaxUploadSizeExceededException must return 413, not 500")
                .isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("250MB");
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

    // ------------------------------------------------------------------
    // handleInvalidCredentials
    // ------------------------------------------------------------------

    @Test
    @DisplayName("InvalidCredentialsException should return 401 Unauthorized")
    void shouldReturn401ForInvalidCredentials() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Credenciales inválidas");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleInvalidCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Credenciales inválidas");
    }

    // ------------------------------------------------------------------
    // handleValidationExceptions
    // ------------------------------------------------------------------

    @Test
    @DisplayName("MethodArgumentNotValidException should return 400 with field errors map")
    void shouldReturn400WithFieldErrorsMap() {
        BindException bindException = new BindException(new Object(), "req");
        bindException.addError(new FieldError("req", "email", "Email must be valid"));
        bindException.addError(new FieldError("req", "name", "Name is required"));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindException);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("email", "Email must be valid");
        assertThat(response.getBody()).containsEntry("name", "Name is required");
    }

    // ------------------------------------------------------------------
    // handleHttpMessageNotReadable — additional branches
    // ------------------------------------------------------------------

    @Test
    @DisplayName("handleHttpMessageNotReadable - Unexpected character cause should return 'Malformed JSON syntax'")
    void shouldReturnMalformedJsonSyntaxForUnexpectedCharacter() {
        RuntimeException cause = new RuntimeException("Unexpected character ('{') at position 5");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", cause);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Malformed JSON syntax");
    }

    @Test
    @DisplayName("handleHttpMessageNotReadable - JSON parse error cause should return 'Invalid JSON format'")
    void shouldReturnInvalidJsonFormatForJsonParseError() {
        RuntimeException cause = new RuntimeException("JSON parse error: unexpected content after root");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", cause);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Invalid JSON format");
    }

    // ------------------------------------------------------------------
    // extractFieldSpecificMessage — additional type branches
    // ------------------------------------------------------------------

    @Test
    @DisplayName("extractFieldSpecificMessage - Integer type should return 'integer value' message")
    void shouldExtractIntegerFieldMessage() {
        String jacksonMsg =
                "Cannot deserialize value of type `java.lang.Integer` from String \"abc\": " +
                "not a valid Integer value (through reference chain: MyDto[\"age\"])";
        InvalidFormatException cause = new InvalidFormatException(null, jacksonMsg, "abc", Integer.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", cause);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message())
                .contains("age")
                .contains("integer value");
    }

    @Test
    @DisplayName("extractFieldSpecificMessage - LocalDate type should return 'valid date' message")
    void shouldExtractLocalDateFieldMessage() {
        String jacksonMsg =
                "Cannot deserialize value of type `java.time.LocalDate` from String \"not-a-date\": " +
                "not a valid date (through reference chain: MyDto[\"birthDate\"])";
        InvalidFormatException cause = new InvalidFormatException(null, jacksonMsg, "not-a-date", java.time.LocalDate.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", cause);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message())
                .contains("birthDate")
                .contains("valid date");
    }

    @Test
    @DisplayName("extractFieldSpecificMessage - unknown type should return 'valid value' message")
    void shouldExtractUnknownTypeFieldMessage() {
        String jacksonMsg =
                "Cannot deserialize value of type `java.util.UUID` from String \"not-uuid\": " +
                "not a valid UUID (through reference chain: MyDto[\"id\"])";
        InvalidFormatException cause = new InvalidFormatException(null, jacksonMsg, "not-uuid", java.util.UUID.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", cause);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message())
                .contains("id")
                .contains("valid value");
    }

    @Test
    @DisplayName("extractFieldSpecificMessage - no reference chain should return fallback message")
    void shouldReturnFallbackWhenNoReferenceChain() {
        String jacksonMsg = "Cannot deserialize value of type `java.math.BigDecimal` from String \"abc\": not a number";
        InvalidFormatException cause = new InvalidFormatException(null, jacksonMsg, "abc", BigDecimal.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", cause);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).contains("Invalid data format");
    }
}
