package usac.cunoc.bpmn.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for marking all notifications as read - matches PDF JSON
 * structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for marking all notifications as read")
public class MarkAllNotificationsReadResponseDto {

    @Schema(description = "Count of notifications marked as read", example = "15")
    private Integer updated_count;

    @Schema(description = "Current unread count after operation", example = "0")
    private Integer unread_count;
}