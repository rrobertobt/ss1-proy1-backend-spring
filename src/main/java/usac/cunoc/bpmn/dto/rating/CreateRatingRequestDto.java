package usac.cunoc.bpmn.dto.rating;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating article rating
 * Matches PDF specification exactly for POST /api/v1/articles/{id}/ratings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create article rating")
public class CreateRatingRequestDto {

    @NotNull(message = "rating es requerido")
    @Min(value = 1, message = "rating debe ser entre 1 y 5")
    @Max(value = 5, message = "rating debe ser entre 1 y 5")
    @Schema(description = "Rating value from 1 to 5", example = "4", required = true)
    private Integer rating;

    @Size(max = 1000, message = "review_text no puede exceder 1000 caracteres")
    @Schema(description = "Review text (optional)", example = "Excelente álbum, muy recomendado")
    private String review_text;
}