package usac.cunoc.bpmn.exception;

/**
 * Exception thrown when event chat operations fail
 */
public class EventChatException extends RuntimeException {

    public EventChatException(String message) {
        super(message);
    }

    public EventChatException(String message, Throwable cause) {
        super(message, cause);
    }

    public static EventChatException chatNotAllowed(Integer eventId) {
        return new EventChatException("Chat is not allowed for event " + eventId);
    }

    public static EventChatException mustBeRegistered(Integer eventId) {
        return new EventChatException("You must be registered for event " + eventId + " to access chat");
    }
}