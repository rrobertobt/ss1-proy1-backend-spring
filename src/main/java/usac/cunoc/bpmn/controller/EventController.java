package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.dto.event.*;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.EventService;
import java.time.LocalDateTime;

/**
 * Event controller for public event operations - matches PDF specification exactly
 */
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Public event operations")
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get events list", description = "Get paginated list of events with filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters")
    })
    public ResponseEntity<ApiResponseDto<EventListResponseDto>> getEvents(
            @RequestParam(required = false) @Parameter(description = "Filter by event status") String status,
            @RequestParam(required = false) @Parameter(description = "Filter upcoming events") Boolean upcoming,
            @RequestParam(required = false) @Parameter(description = "Filter past events") Boolean past,
            @RequestParam(defaultValue = "1") @Parameter(description = "Page number") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Items per page") Integer limit,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        EventListResponseDto events = eventService.getEvents(status, upcoming, past, page, limit, userId);
        return ResponseEntity.ok(ApiResponseDto.success(events));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event details", description = "Get detailed information about a specific event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<ApiResponseDto<EventDetailResponseDto>> getEventById(
            @PathVariable @Parameter(description = "Event ID") Integer id,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        EventDetailResponseDto event = eventService.getEventById(id, userId);
        return ResponseEntity.ok(ApiResponseDto.success(event));
    }

    @PostMapping("/{id}/register")
    @Operation(summary = "Register to event", description = "Register current user to an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Registration not allowed or user already registered"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<ApiResponseDto<EventRegistrationResponseDto>> registerToEvent(
            @PathVariable @Parameter(description = "Event ID") Integer id,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        EventRegistrationResponseDto registration = eventService.registerToEvent(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Registrado al evento exitosamente", registration));
    }

    @DeleteMapping("/{id}/register")
    @Operation(summary = "Unregister from event", description = "Unregister current user from an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unregistration successful"),
            @ApiResponse(responseCode = "400", description = "User not registered for this event"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<ApiResponseDto<EventUnregistrationResponseDto>> unregisterFromEvent(
            @PathVariable @Parameter(description = "Event ID") Integer id,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        EventUnregistrationResponseDto unregistration = eventService.unregisterFromEvent(id, userId);
        return ResponseEntity.ok(ApiResponseDto.success("Registro cancelado exitosamente", unregistration));
    }

    @GetMapping("/{id}/chat")
    @Operation(summary = "Get event chat", description = "Get chat messages for an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat messages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Must be registered to access chat"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<ApiResponseDto<EventChatResponseDto>> getEventChat(
            @PathVariable @Parameter(description = "Event ID") Integer id,
            @RequestParam(defaultValue = "1") @Parameter(description = "Page number") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Items per page") Integer limit,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Get messages since this timestamp") LocalDateTime since,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        EventChatResponseDto chat = eventService.getEventChat(id, page, limit, since, userId);
        return ResponseEntity.ok(ApiResponseDto.success(chat));
    }

    @PostMapping("/{id}/chat")
    @Operation(summary = "Send chat message", description = "Send a message to event chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid message content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Must be registered to send messages or chat not allowed"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<ApiResponseDto<SendChatMessageResponseDto>> sendChatMessage(
            @PathVariable @Parameter(description = "Event ID") Integer id,
            @Valid @RequestBody SendChatMessageRequestDto request,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        SendChatMessageResponseDto message = eventService.sendChatMessage(id, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Mensaje enviado exitosamente", message));
    }

    // PRIVATE HELPER METHODS

    private Integer getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // For public endpoints that don't require authentication
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsernameOrEmail(userDetails.getUsername())
                .map(User::getId)
                .orElse(null);
    }
}