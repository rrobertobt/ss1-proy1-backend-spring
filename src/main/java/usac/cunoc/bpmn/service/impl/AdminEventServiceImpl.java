package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.admin.event.*;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.exception.BusinessValidationException;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.AdminEventService;
import java.time.LocalDateTime;

/**
 * Admin event service implementation - 100% compliant with PDF specification
 * and database schema
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final EventStatusRepository eventStatusRepository;
    private final AnalogArticleRepository analogArticleRepository;
    private final UserRepository userRepository;

    @Override
    public CreateEventResponseDto createEvent(CreateEventRequestDto request, Integer adminUserId) {
        log.info("Creating new event: {} by admin user: {}", request.getTitle(), adminUserId);

        // Validate admin user exists
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        // Get default event status (assuming "Programado" is default)
        EventStatus defaultStatus = eventStatusRepository.findByName("Programado")
                .orElseThrow(() -> new RuntimeException("Default event status 'Programado' not found"));

        // Validate article exists if provided
        AnalogArticle article = null;
        if (request.getArticleId() != null) {
            article = analogArticleRepository.findById(request.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article not found with ID: " + request.getArticleId()));
        }

        // Create new event entity
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventStatus(defaultStatus);
        event.setAnalogArticle(article);
        event.setAudioFileUrl(request.getAudioFileUrl());
        event.setAudioDurationSeconds(request.getAudioDuration());
        event.setStartDatetime(request.getStartDatetime());
        event.setEndDatetime(request.getEndDatetime());
        event.setMaxParticipants(request.getMaxParticipants());
        event.setCurrentParticipants(0);
        event.setAllowChat(request.getAllowChat() != null ? request.getAllowChat() : true);
        event.setCreatedByUser(adminUser);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        Event savedEvent = eventRepository.save(event);

        log.info("Event created successfully with ID: {}", savedEvent.getId());

        return new CreateEventResponseDto(
                savedEvent.getId(),
                savedEvent.getTitle(),
                savedEvent.getDescription(),
                savedEvent.getAnalogArticle() != null ? savedEvent.getAnalogArticle().getId() : null,
                savedEvent.getStartDatetime(),
                savedEvent.getEndDatetime(),
                savedEvent.getEventStatus().getName(),
                savedEvent.getCreatedAt());
    }

    @Override
    public UpdateEventResponseDto updateEvent(Integer eventId, UpdateEventRequestDto request,
            Integer adminUserId) {
        log.info("Updating event {} by admin user: {}", eventId, adminUserId);

        // Validate admin user exists
        @SuppressWarnings("unused")
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        // Find existing event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Update only provided fields
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }

        if (request.getArticleId() != null) {
            AnalogArticle article = analogArticleRepository.findById(request.getArticleId())
                    .orElseThrow(() -> new RuntimeException("Article not found with ID: " + request.getArticleId()));
            event.setAnalogArticle(article);
        }

        if (request.getAudioFileUrl() != null) {
            event.setAudioFileUrl(request.getAudioFileUrl());
        }

        if (request.getAudioDuration() != null) {
            event.setAudioDurationSeconds(request.getAudioDuration());
        }

        if (request.getStartDatetime() != null) {
            event.setStartDatetime(request.getStartDatetime());
        }

        if (request.getEndDatetime() != null) {
            event.setEndDatetime(request.getEndDatetime());
        }

        if (request.getMaxParticipants() != null) {
            // Validate that new max is not less than current participants
            if (request.getMaxParticipants() < event.getCurrentParticipants()) {
                throw BusinessValidationException.maxParticipantsBelowCurrent(
                        request.getMaxParticipants(), event.getCurrentParticipants());
            }
            event.setMaxParticipants(request.getMaxParticipants());
        }

        if (request.getAllowChat() != null) {
            event.setAllowChat(request.getAllowChat());
        }

        // Update timestamp
        event.setUpdatedAt(LocalDateTime.now());

        Event savedEvent = eventRepository.save(event);

        log.info("Event {} updated successfully", eventId);

        return new UpdateEventResponseDto(
                savedEvent.getId(),
                savedEvent.getTitle(),
                savedEvent.getDescription(),
                savedEvent.getAnalogArticle() != null ? savedEvent.getAnalogArticle().getId() : null,
                savedEvent.getStartDatetime(),
                savedEvent.getEndDatetime(),
                savedEvent.getEventStatus().getName(),
                savedEvent.getUpdatedAt());
    }
}