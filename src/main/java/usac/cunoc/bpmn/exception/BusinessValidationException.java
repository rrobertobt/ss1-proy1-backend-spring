package usac.cunoc.bpmn.exception;

/**
 * Exception thrown when business validation rules are violated
 */
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BusinessValidationException maxParticipantsBelowCurrent(Integer maxParticipants, Integer currentParticipants) {
        return new BusinessValidationException(
            "Cannot set max participants (" + maxParticipants + ") below current participant count (" + currentParticipants + ")");
    }
}