package co.com.proptech.api.exception;

import co.com.proptech.model.exceptions.UnauthorizedOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles business validation errors (e.g., invalid state transitions, rule violations).
     * Returns 400 Bad Request instead of 500 Internal Server Error.
     * 
     * Common scenarios:
     * - User role validation (e.g., "Only landlords can publish properties")
     * - Invalid state transitions
     * - Business rule violations
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles JSON deserialization errors (e.g., invalid data types, malformed JSON).
     * Returns 400 Bad Request instead of 500 Internal Server Error.
     * 
     * Common scenarios:
     * - Non-numeric value for BigDecimal/Integer fields
     * - Invalid date format
     * - Malformed JSON syntax
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Invalid request body";
        
        // Extract more specific error message if available
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String causeMessage = ex.getCause().getMessage();
            
            // Parse field name from Jackson error message
            // Example: "Cannot deserialize value of type `java.math.BigDecimal` from String \"not-a-number\": not a valid representation
            //           at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 5, column: 13] (through reference chain: co.com.proptech.api.dto.PublishPropertyRequestDto[\"price\"])"
            if (causeMessage.contains("Cannot deserialize value")) {
                message = extractFieldSpecificMessage(causeMessage);
            } else if (causeMessage.contains("Unexpected character")) {
                message = "Malformed JSON syntax";
            } else if (causeMessage.contains("JSON parse error")) {
                message = "Invalid JSON format";
            }
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }
    
    /**
     * Extracts field-specific error message from Jackson deserialization error.
     */
    private String extractFieldSpecificMessage(String causeMessage) {
        try {
            // Extract field name from reference chain: ["fieldName"]
            if (causeMessage.contains("reference chain:") && causeMessage.contains("[\"")) {
                int startIdx = causeMessage.lastIndexOf("[\"") + 2;
                int endIdx = causeMessage.indexOf("\"]", startIdx);
                if (startIdx > 1 && endIdx > startIdx) {
                    String fieldName = causeMessage.substring(startIdx, endIdx);
                    
                    // Extract expected type
                    String expectedType = "valid value";
                    if (causeMessage.contains("type `java.math.BigDecimal`")) {
                        expectedType = "numeric value";
                    } else if (causeMessage.contains("type `java.lang.Integer`") || causeMessage.contains("type `int`")) {
                        expectedType = "integer value";
                    } else if (causeMessage.contains("type `java.time.LocalDate`")) {
                        expectedType = "valid date";
                    }
                    
                    return String.format("Invalid value for field '%s'. Expected %s.", fieldName, expectedType);
                }
            }
        } catch (Exception e) {
            // Fallback to generic message if parsing fails
        }
        
        return "Invalid data format in request body. Please check field types.";
    }

    /**
     * Handles authorization failures when user lacks required permissions.
     * Returns 403 Forbidden instead of 500 Internal Server Error.
     * 
     * Common scenarios:
     * - User without LANDLORD role attempting to publish property
     * - User trying to access resource they don't own
     * - Missing required authority for @PreAuthorize methods
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Access denied. You do not have permission to perform this action."));
    }

    /**
     * Handles business logic authorization failures (domain layer).
     * Returns 403 Forbidden when domain rules prevent an operation due to user role/permissions.
     * 
     * Common scenarios:
     * - TENANT user trying to publish a property (only LANDLORD allowed)
     * - User trying to modify resources they don't own
     * - Business rule: "Only X role can perform Y action"
     */
    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedOperation(UnauthorizedOperationException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred"));
    }

    record ErrorResponse(String message) {}
}
