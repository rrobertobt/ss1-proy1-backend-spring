package usac.cunoc.bpmn.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for marking notification as read - matches PDF JSON structure
 * exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for marking notification as read")
public class MarkNotificationReadResponseDto {

    @Schema(description = "Notification ID", example = "1")
    private Integer notificationId;

    @Schema(description = "Whether notification is read", example = "true")
    private Boolean isRead;

    @Schema(description = "Timestamp when notification was marked as read")
    private LocalDateTime readAt;
}