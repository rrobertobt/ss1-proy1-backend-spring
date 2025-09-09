package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.admin.event.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.AdminEventService;

/**
 * Admin event controller for administrative event operations - matches PDF
 * specification exactly
 * Requires ADMIN role for all operations
 */
@RestController
@RequestMapping("/api/v1/admin/events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Admin Event Management", description = "Administrative event management operations")
public class AdminEventController {

        private final AdminEventService adminEventService;
        private final UserRepository userRepository;

        @PostMapping
        @Operation(summary = "Create new event", description = "Create a new event in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Event created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        })
        public ResponseEntity<ApiResponseDto<CreateEventResponseDto>> createEvent(
                        @Valid @RequestBody CreateEventRequestDto request,
                        Authentication authentication) {

                Integer adminUserId = getCurrentUserId(authentication);
                CreateEventResponseDto response = adminEventService.createEvent(request, adminUserId);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseDto.success("Evento creado exitosamente", response));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update event", description = "Update an existing event")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Event updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
                        @ApiResponse(responseCode = "404", description = "Event not found")
        })
        public ResponseEntity<ApiResponseDto<UpdateEventResponseDto>> updateEvent(
                        @PathVariable @Parameter(description = "Event ID") Integer id,
                        @Valid @RequestBody UpdateEventRequestDto request,
                        Authentication authentication) {

                Integer adminUserId = getCurrentUserId(authentication);
                UpdateEventResponseDto response = adminEventService.updateEvent(id, request, adminUserId);

                return ResponseEntity.ok(ApiResponseDto.success("Evento actualizado exitosamente", response));
        }

        // PRIVATE HELPER METHODS

        private Integer getCurrentUserId(Authentication authentication) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return userRepository.findByUsernameOrEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Admin user not found"))
                                .getId();
        }
}