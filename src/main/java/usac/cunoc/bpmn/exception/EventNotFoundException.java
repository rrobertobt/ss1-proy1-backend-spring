package usac.cunoc.bpmn.exception;

/**
 * Exception thrown when an event is not found
 */
public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(String message) {
        super(message);
    }

    public EventNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventNotFoundException(Integer eventId) {
        super("Event not found with ID: " + eventId);
    }
}