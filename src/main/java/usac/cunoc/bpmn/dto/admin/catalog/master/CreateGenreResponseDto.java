package usac.cunoc.bpmn.dto.admin.catalog.master;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Create genre response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when creating a new music genre")
public class CreateGenreResponseDto {

    @Schema(description = "Genre ID", example = "15")
    private Integer id;

    @Schema(description = "Genre name", example = "Rock Alternativo")
    private String name;

    @Schema(description = "Genre description", example = "Subgenero del rock con influencias alternativas")
    private String description;

    @Schema(description = "Creation timestamp", example = "2025-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;
}