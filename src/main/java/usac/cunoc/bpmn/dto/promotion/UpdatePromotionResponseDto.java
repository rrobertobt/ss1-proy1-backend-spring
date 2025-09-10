package usac.cunoc.bpmn.dto.promotion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for updating CD promotion - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when updating a CD promotion")
public class UpdatePromotionResponseDto {

    @Schema(description = "Updated promotion ID", example = "1")
    private Integer id;

    @Schema(description = "Updated promotion name", example = "Promoción Rock Actualizada")
    private String name;

    @Schema(description = "Update timestamp")
    private LocalDateTime updatedAt;
}