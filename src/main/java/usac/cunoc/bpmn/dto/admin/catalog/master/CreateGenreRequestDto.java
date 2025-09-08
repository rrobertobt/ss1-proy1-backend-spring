package usac.cunoc.bpmn.dto.admin.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create genre request DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new music genre")
public class CreateGenreRequestDto {

    @NotBlank(message = "Genre name is required")
    @Size(max = 100, message = "Genre name cannot exceed 100 characters")
    @Schema(description = "Genre name", example = "Rock Alternativo", required = true)
    private String name;

    @Schema(description = "Genre description", example = "Subgenero del rock con influencias alternativas")
    private String description;
}