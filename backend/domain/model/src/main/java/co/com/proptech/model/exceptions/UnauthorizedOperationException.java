package co.com.proptech.model.exceptions;

/**
 * Exception thrown when a user attempts an action they are not authorized to perform.
 * This represents a business rule violation related to permissions/roles.
 * 
 * Should be mapped to HTTP 403 Forbidden in the REST API layer.
 * 
 * Examples:
 * - User with TENANT role trying to publish a property
 * - User trying to access or modify resources they don't own
 */
public class UnauthorizedOperationException extends RuntimeException {
    
    public UnauthorizedOperationException(String message) {
        super(message);
    }
    
    public UnauthorizedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
