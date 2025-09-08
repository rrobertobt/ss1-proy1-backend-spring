package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.admin.event.*;

/**
 * Admin event service interface for administrative event operations
 */
public interface AdminEventService {

    /**
     * Create a new event
     */
    CreateEventResponseDto createEvent(CreateEventRequestDto request, Integer adminUserId);

    /**
     * Update an existing event
     */
    UpdateEventResponseDto updateEvent(Integer eventId, UpdateEventRequestDto request, 
                                       Integer adminUserId);
}