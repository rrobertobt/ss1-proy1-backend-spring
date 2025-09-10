package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.dto.notification.NotificationListResponseDto;
import usac.cunoc.bpmn.dto.notification.MarkNotificationReadResponseDto;
import usac.cunoc.bpmn.dto.notification.MarkAllNotificationsReadResponseDto;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.NotificationService;

/**
 * Notification controller for user notification operations - matches PDF
 * specification exactly
 * Handles user notifications, mark as read operations, and pagination
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "User notification management operations")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Get current user's notifications with pagination and optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponseDto<NotificationListResponseDto>> getUserNotifications(
            @Parameter(description = "Page number (1-based)", example = "1") @RequestParam(required = false) Integer page,

            @Parameter(description = "Items per page (max 100)", example = "10") @RequestParam(required = false) Integer limit,

            @Parameter(description = "Filter by unread status (true=unread only, false=read only, null=all)", example = "true") @RequestParam(required = false) Boolean unread,

            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        NotificationListResponseDto response = notificationService.getUserNotifications(userId, page, limit, unread);

        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Notification not found or access denied")
    })
    public ResponseEntity<ApiResponseDto<MarkNotificationReadResponseDto>> markNotificationAsRead(
            @Parameter(description = "Notification ID", example = "1") @PathVariable Integer id,

            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        MarkNotificationReadResponseDto response = notificationService.markNotificationAsRead(id, userId);

        return ResponseEntity.ok(ApiResponseDto.success("Notificación marcada como leída", response));
    }

    @PutMapping("/mark-all-read")
    @Operation(summary = "Mark all notifications as read", description = "Mark all user notifications as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All notifications marked as read successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponseDto<MarkAllNotificationsReadResponseDto>> markAllNotificationsAsRead(
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        MarkAllNotificationsReadResponseDto response = notificationService.markAllNotificationsAsRead(userId);

        return ResponseEntity.ok(ApiResponseDto.success("Todas las notificaciones marcadas como leídas", response));
    }

    /**
     * Extract user ID from authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
}