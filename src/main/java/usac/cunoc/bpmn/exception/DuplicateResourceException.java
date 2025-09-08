package usac.cunoc.bpmn.exception;

/**
 * Exception thrown when trying to create a resource that already exists
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new DuplicateResourceException with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateResourceException with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DuplicateResourceException with the specified cause.
     *
     * @param cause the cause
     */
    public DuplicateResourceException(Throwable cause) {
        super(cause);
    }
}