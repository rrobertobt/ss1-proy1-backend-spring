package usac.cunoc.bpmn.dto.admin.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for create event response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create event response data")
public class CreateEventResponseDto {

    @Schema(description = "Event ID", example = "1")
    private Integer id;

    @Schema(description = "Event title", example = "Jazz Night")
    private String title;

    @Schema(description = "Event description", example = "An evening of classic jazz")
    private String description;

    @Schema(description = "Associated article ID", example = "1")
    private Integer article_id;

    @Schema(description = "Event start date and time")
    private LocalDateTime start_datetime;

    @Schema(description = "Event end date and time")
    private LocalDateTime end_datetime;

    @Schema(description = "Event status", example = "Programado")
    private String status;

    @Schema(description = "Event creation date")
    private LocalDateTime created_at;
}