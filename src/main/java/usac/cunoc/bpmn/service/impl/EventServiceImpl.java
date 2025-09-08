package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.event.*;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.exception.EventChatException;
import usac.cunoc.bpmn.exception.EventNotFoundException;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.EventService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Event service implementation - 100% compliant with PDF specification and database schema
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventChatMessageRepository eventChatMessageRepository;
    private final UserRepository userRepository;
    private final EventStatusRepository eventStatusRepository;

    @Override
    public EventListResponseDto getEvents(String status, Boolean upcoming, Boolean past, 
                                          Integer page, Integer limit, Integer userId) {
        log.info("Getting events with filters - status: {}, upcoming: {}, past: {}, page: {}, limit: {}", 
                 status, upcoming, past, page, limit);

        Pageable pageable = PageRequest.of(page - 1, limit);
        LocalDateTime currentTime = LocalDateTime.now();

        Page<Event> eventPage = eventRepository.findEventsWithFilters(
            status, upcoming, past, currentTime, pageable);

        List<EventListResponseDto.EventDto> eventDtos = eventPage.getContent().stream()
            .map(event -> mapToEventDto(event, userId))
            .collect(Collectors.toList());

        EventListResponseDto.PaginationDto pagination = new EventListResponseDto.PaginationDto(
            eventPage.getNumber() + 1,
            eventPage.getTotalPages(),
            (int) eventPage.getTotalElements(),
            eventPage.getSize()
        );

        return new EventListResponseDto(eventDtos, pagination);
    }

    @Override
    public EventDetailResponseDto getEventById(Integer eventId, Integer userId) {
        log.info("Getting event details for ID: {} by user: {}", eventId, userId);

        Event event = eventRepository.findByIdWithDetails(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        return mapToEventDetailDto(event, userId);
    }

    @Override
    @Transactional
    public EventRegistrationResponseDto registerToEvent(Integer eventId, Integer userId) {
        log.info("Registering user {} to event {}", userId, eventId);

        // Validate event exists and allows registration
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getEventStatus().getAllowsRegistration()) {
            throw new RuntimeException("Registration is not allowed for this event");
        }

        // Check if already registered
        if (eventRegistrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new RuntimeException("User is already registered for this event");
        }

        // Get user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Create registration - triggers will handle participant count
        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setRegisteredAt(LocalDateTime.now());

        EventRegistration savedRegistration = eventRegistrationRepository.save(registration);

        return new EventRegistrationResponseDto(
            eventId,
            userId,
            savedRegistration.getRegisteredAt()
        );
    }

    @Override
    @Transactional
    public EventUnregistrationResponseDto unregisterFromEvent(Integer eventId, Integer userId) {
        log.info("Unregistering user {} from event {}", userId, eventId);

        // Validate registration exists
        EventRegistration registration = eventRegistrationRepository.findByEventIdAndUserId(eventId, userId)
            .orElseThrow(() -> new RuntimeException("User is not registered for this event"));

        // Delete registration - triggers will handle participant count
        eventRegistrationRepository.delete(registration);

        // Get updated participant count
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        return new EventUnregistrationResponseDto(
            eventId,
            userId,
            LocalDateTime.now(),
            event.getCurrentParticipants()
        );
    }

    @Override
    public EventChatResponseDto getEventChat(Integer eventId, Integer page, Integer limit, 
                                             LocalDateTime since, Integer userId) {
        log.info("Getting chat for event {} - page: {}, limit: {}, since: {}", eventId, page, limit, since);

        // Validate event exists
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if user is registered (required for chat access)
        if (userId != null && !eventRegistrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new RuntimeException("You must be registered for this event to access chat");
        }

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<EventChatMessage> messagePage = eventChatMessageRepository.findChatMessagesByEventId(
            eventId, since, pageable);

        List<EventChatResponseDto.MessageDto> messageDtos = messagePage.getContent().stream()
            .map(this::mapToMessageDto)
            .collect(Collectors.toList());

        EventChatResponseDto.PaginationDto pagination = new EventChatResponseDto.PaginationDto(
            messagePage.getNumber() + 1,
            messagePage.getTotalPages(),
            (int) messagePage.getTotalElements(),
            messagePage.getSize()
        );

        return new EventChatResponseDto(eventId, messageDtos, pagination);
    }

    @Override
    @Transactional
    public SendChatMessageResponseDto sendChatMessage(Integer eventId, SendChatMessageRequestDto request, 
                                                      Integer userId) {
        log.info("Sending chat message to event {} by user {}", eventId, userId);

        // Validate event exists and allows chat
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!event.getAllowChat()) {
            throw EventChatException.chatNotAllowed(eventId);
        }

        // Check if user is registered
        if (!eventRegistrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw EventChatException.mustBeRegistered(eventId);
        }

        // Get user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Create message
        EventChatMessage message = new EventChatMessage();
        message.setEvent(event);
        message.setUser(user);
        message.setMessage(request.getMessage());
        message.setIsSystemMessage(false);
        message.setSentAt(LocalDateTime.now());

        EventChatMessage savedMessage = eventChatMessageRepository.save(message);

        return new SendChatMessageResponseDto(
            savedMessage.getId(),
            eventId,
            userId,
            savedMessage.getMessage(),
            savedMessage.getSentAt()
        );
    }

    // PRIVATE MAPPING METHODS

    private EventListResponseDto.EventDto mapToEventDto(Event event, Integer userId) {
        EventListResponseDto.ArticleDto articleDto = null;
        if (event.getAnalogArticle() != null) {
            articleDto = new EventListResponseDto.ArticleDto(
                event.getAnalogArticle().getId(),
                event.getAnalogArticle().getTitle(),
                event.getAnalogArticle().getArtist().getName(),
                event.getAnalogArticle().getImageUrl()
            );
        }

        Boolean isRegistered = false;
        if (userId != null) {
            isRegistered = eventRegistrationRepository.existsByEventIdAndUserId(event.getId(), userId);
        }

        return new EventListResponseDto.EventDto(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            articleDto,
            event.getStartDatetime(),
            event.getEndDatetime(),
            event.getAudioDurationSeconds(),
            event.getMaxParticipants(),
            event.getCurrentParticipants(),
            event.getEventStatus().getName(),
            event.getAllowChat(),
            isRegistered
        );
    }

    private EventDetailResponseDto mapToEventDetailDto(Event event, Integer userId) {
        EventDetailResponseDto.StatusDto statusDto = new EventDetailResponseDto.StatusDto(
            event.getEventStatus().getId(),
            event.getEventStatus().getName(),
            event.getEventStatus().getAllowsRegistration()
        );

        EventDetailResponseDto.ArticleDto articleDto = null;
        if (event.getAnalogArticle() != null) {
            EventDetailResponseDto.ArtistDto artistDto = new EventDetailResponseDto.ArtistDto(
                event.getAnalogArticle().getArtist().getId(),
                event.getAnalogArticle().getArtist().getName()
            );

            articleDto = new EventDetailResponseDto.ArticleDto(
                event.getAnalogArticle().getId(),
                event.getAnalogArticle().getTitle(),
                artistDto,
                event.getAnalogArticle().getImageUrl()
            );
        }

        EventDetailResponseDto.CreatedByDto createdByDto = new EventDetailResponseDto.CreatedByDto(
            event.getCreatedByUser().getId(),
            event.getCreatedByUser().getUsername()
        );

        Boolean isRegistered = false;
        if (userId != null) {
            isRegistered = eventRegistrationRepository.existsByEventIdAndUserId(event.getId(), userId);
        }

        // Get registered participants
        List<EventRegistration> registrations = eventRegistrationRepository.findRegisteredParticipantsByEventId(event.getId());
        List<EventDetailResponseDto.ParticipantDto> participants = registrations.stream()
            .map(reg -> new EventDetailResponseDto.ParticipantDto(
                reg.getUser().getId(),
                reg.getUser().getUsername(),
                reg.getRegisteredAt()
            ))
            .collect(Collectors.toList());

        return new EventDetailResponseDto(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            statusDto,
            articleDto,
            event.getAudioFileUrl(),
            event.getAudioDurationSeconds(),
            event.getStartDatetime(),
            event.getEndDatetime(),
            event.getMaxParticipants(),
            event.getCurrentParticipants(),
            event.getAllowChat(),
            createdByDto,
            isRegistered,
            participants,
            event.getCreatedAt()
        );
    }

    private EventChatResponseDto.MessageDto mapToMessageDto(EventChatMessage message) {
        EventChatResponseDto.UserDto userDto = new EventChatResponseDto.UserDto(
            message.getUser().getId(),
            message.getUser().getUsername()
        );

        return new EventChatResponseDto.MessageDto(
            message.getId(),
            userDto,
            message.getMessage(),
            message.getIsSystemMessage(),
            message.getSentAt()
        );
    }
}