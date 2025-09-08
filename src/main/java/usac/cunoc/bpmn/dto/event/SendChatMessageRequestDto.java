package usac.cunoc.bpmn.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending chat message request - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Send chat message request data")
public class SendChatMessageRequestDto {

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    @Schema(description = "Chat message content", example = "Hello everyone!", required = true)
    private String message;
}