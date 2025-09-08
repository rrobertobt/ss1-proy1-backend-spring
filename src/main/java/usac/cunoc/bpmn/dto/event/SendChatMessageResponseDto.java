package usac.cunoc.bpmn.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for send chat message response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Send chat message response data")
public class SendChatMessageResponseDto {

    @Schema(description = "Message ID", example = "1")
    private Integer messageId;

    @Schema(description = "Event ID", example = "1")
    private Integer eventId;

    @Schema(description = "User ID", example = "2")
    private Integer userId;

    @Schema(description = "Message content", example = "Hello everyone!")
    private String message;

    @Schema(description = "Message sent date and time")
    private LocalDateTime sentAt;
}