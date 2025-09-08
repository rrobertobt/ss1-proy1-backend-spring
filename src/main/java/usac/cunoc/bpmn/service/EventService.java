package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.event.*;
import java.time.LocalDateTime;

/**
 * Event service interface for public event operations
 */
public interface EventService {

    /**
     * Get paginated list of events with filters
     */
    EventListResponseDto getEvents(String status, Boolean upcoming, Boolean past, 
                                   Integer page, Integer limit, Integer userId);

    /**
     * Get event details by ID
     */
    EventDetailResponseDto getEventById(Integer eventId, Integer userId);

    /**
     * Register user to an event
     */
    EventRegistrationResponseDto registerToEvent(Integer eventId, Integer userId);

    /**
     * Unregister user from an event
     */
    EventUnregistrationResponseDto unregisterFromEvent(Integer eventId, Integer userId);

    /**
     * Get chat messages for an event
     */
    EventChatResponseDto getEventChat(Integer eventId, Integer page, Integer limit, 
                                      LocalDateTime since, Integer userId);

    /**
     * Send chat message to an event
     */
    SendChatMessageResponseDto sendChatMessage(Integer eventId, SendChatMessageRequestDto request, 
                                               Integer userId);
}