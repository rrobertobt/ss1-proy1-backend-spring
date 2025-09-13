package usac.cunoc.bpmn.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for notification list - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for user notifications list")
public class NotificationListResponseDto {

    @Schema(description = "List of user notifications")
    private List<NotificationDto> notifications;

    @Schema(description = "Count of unread notifications", example = "5")
    private Integer unread_count;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Individual notification information")
    public static class NotificationDto {
        @Schema(description = "Notification ID", example = "1")
        private Integer id;

        @Schema(description = "Notification type")
        private NotificationTypeDto type;

        @Schema(description = "Notification title", example = "Nuevo artículo disponible")
        private String title;

        @Schema(description = "Notification message", example = "El vinilo que esperabas ya está disponible")
        private String message;

        @Schema(description = "Reference type", example = "order")
        private String reference_type;

        @Schema(description = "Reference ID", example = "123")
        private Integer reference_id;

        @Schema(description = "Whether notification is read", example = "false")
        private Boolean is_read;

        @Schema(description = "Whether email was sent", example = "true")
        private Boolean is_email_sent;

        @Schema(description = "Notification creation date")
        private LocalDateTime created_at;

        @Schema(description = "Notification read date")
        private LocalDateTime read_at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Notification type information")
    public static class NotificationTypeDto {
        @Schema(description = "Notification type ID", example = "1")
        private Integer id;

        @Schema(description = "Notification type name", example = "Preventa Disponible")
        private String name;
    }
}