package usac.cunoc.bpmn.dto.preorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for preorder audio download action
 * Matches PDF specification exactly for POST
 * /api/v1/preorder-audios/{id}/download
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Audio download response")
public class PreorderAudioDownloadResponseDto {

    @Schema(description = "Audio ID", example = "1")
    private Integer audioId;

    @Schema(description = "Download URL", example = "https://cdn.example.com/download/audio123")
    private String download_url;

    @Schema(description = "Download URL expiration time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expires_at;

    @Schema(description = "Download status", example = "true")
    private Boolean downloaded;

    @Schema(description = "Downloaded timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime downloaded_at;
}