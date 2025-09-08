package usac.cunoc.bpmn.dto.admin.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for update event request - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update event request data")
public class UpdateEventRequestDto {

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Schema(description = "Event title", example = "Jazz Night")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Schema(description = "Event description", example = "An evening of classic jazz")
    private String description;

    @Schema(description = "Associated article ID", example = "1")
    private Integer articleId;

    @Size(max = 500, message = "Audio file URL cannot exceed 500 characters")
    @Schema(description = "Audio file URL")
    private String audioFileUrl;

    @Positive(message = "Audio duration must be positive")
    @Schema(description = "Audio duration in seconds", example = "3600")
    private Integer audioDuration;

    @Schema(description = "Event start date and time")
    private LocalDateTime startDatetime;

    @Schema(description = "Event end date and time")
    private LocalDateTime endDatetime;

    @Positive(message = "Max participants must be positive")
    @Schema(description = "Maximum number of participants", example = "100")
    private Integer maxParticipants;

    @Schema(description = "Whether chat is allowed", example = "true")
    private Boolean allowChat;

    // Custom validation to ensure end is after start when both are provided
    @AssertTrue(message = "End datetime must be after start datetime")
    public boolean isEndAfterStart() {
        if (startDatetime == null || endDatetime == null) {
            return true; // Skip validation if either is null
        }
        return endDatetime.isAfter(startDatetime);
    }
}