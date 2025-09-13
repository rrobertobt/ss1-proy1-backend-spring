package usac.cunoc.bpmn.dto.preorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for preorder audio play action
 * Matches PDF specification exactly for POST /api/v1/preorder-audios/{id}/play
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Audio play registration response")
public class PreorderAudioPlayResponseDto {

    @Schema(description = "Audio ID", example = "1")
    private Integer audioId;

    @Schema(description = "Current play count", example = "6")
    private Integer play_count;

    @Schema(description = "Last played timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime last_played_at;
}