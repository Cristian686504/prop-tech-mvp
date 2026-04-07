package co.com.proptech.model.exceptions;

/**
 * Exception thrown when attempting to register a user with an email that is
 * already associated with an existing account.
 *
 * Should be mapped to HTTP 409 Conflict in the REST API layer.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
