package usac.cunoc.bpmn.exception;

/**
 * Exception thrown when event registration fails
 */
public class EventRegistrationException extends RuntimeException {

    public EventRegistrationException(String message) {
        super(message);
    }

    public EventRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static EventRegistrationException alreadyRegistered(Integer eventId, Integer userId) {
        return new EventRegistrationException("User " + userId + " is already registered for event " + eventId);
    }

    public static EventRegistrationException registrationNotAllowed(Integer eventId) {
        return new EventRegistrationException("Registration is not allowed for event " + eventId);
    }

    public static EventRegistrationException eventFull(Integer eventId) {
        return new EventRegistrationException("Event " + eventId + " has reached maximum capacity");
    }

    public static EventRegistrationException notRegistered(Integer eventId, Integer userId) {
        return new EventRegistrationException("User " + userId + " is not registered for event " + eventId);
    }
}