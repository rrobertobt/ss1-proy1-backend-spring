package usac.cunoc.bpmn.dto.admin.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for create event request - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create event request data")
public class CreateEventRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Schema(description = "Event title", example = "Jazz Night", required = true)
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Schema(description = "Event description", example = "An evening of classic jazz")
    private String description;

    @Schema(description = "Associated article ID", example = "1")
    private Integer articleId;

    @NotBlank(message = "Audio file URL is required")
    @Size(max = 500, message = "Audio file URL cannot exceed 500 characters")
    @Schema(description = "Audio file URL", required = true)
    private String audioFileUrl;

    @NotNull(message = "Audio duration is required")
    @Positive(message = "Audio duration must be positive")
    @Schema(description = "Audio duration in seconds", example = "3600", required = true)
    private Integer audioDuration;

    @NotNull(message = "Start datetime is required")
    @Future(message = "Start datetime must be in the future")
    @Schema(description = "Event start date and time", required = true)
    private LocalDateTime startDatetime;

    @NotNull(message = "End datetime is required")
    @Schema(description = "Event end date and time", required = true)
    private LocalDateTime endDatetime;

    @Positive(message = "Max participants must be positive")
    @Schema(description = "Maximum number of participants", example = "100")
    private Integer maxParticipants;

    @Schema(description = "Whether chat is allowed", example = "true")
    private Boolean allowChat = true;

    // Custom validation to ensure end is after start
    @AssertTrue(message = "End datetime must be after start datetime")
    public boolean isEndAfterStart() {
        if (startDatetime == null || endDatetime == null) {
            return true; // Let @NotNull handle null validation
        }
        return endDatetime.isAfter(startDatetime);
    }
}