package usac.cunoc.bpmn.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for event registration response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Event registration response data")
public class EventRegistrationResponseDto {

    @Schema(description = "Event ID", example = "1")
    private Integer eventId;

    @Schema(description = "User ID", example = "2")
    private Integer userId;

    @Schema(description = "Registration date and time")
    private LocalDateTime registeredAt;
}