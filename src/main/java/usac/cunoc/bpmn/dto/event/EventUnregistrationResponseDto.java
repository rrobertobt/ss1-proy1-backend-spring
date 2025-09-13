package usac.cunoc.bpmn.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for event unregistration response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Event unregistration response data")
public class EventUnregistrationResponseDto {

    @Schema(description = "Event ID", example = "1")
    private Integer event_id;

    @Schema(description = "User ID", example = "2")
    private Integer user_id;

    @Schema(description = "Cancellation date and time")
    private LocalDateTime cancelled_at;

    @Schema(description = "New number of participants after cancellation", example = "44")
    private Integer new_participants_count;
}