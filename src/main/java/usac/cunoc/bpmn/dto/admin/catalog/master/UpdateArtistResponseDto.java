package usac.cunoc.bpmn.dto.admin.catalog.master;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Update artist response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when updating an artist")
public class UpdateArtistResponseDto {

    @Schema(description = "Artist ID", example = "1")
    private Integer id;

    @Schema(description = "Artist name", example = "The Beatles")
    private String name;

    @Schema(description = "Update timestamp", example = "2025-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated_at;
}